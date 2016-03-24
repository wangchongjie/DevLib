package com.baidu.unbiz.devlib.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.baidu.unbiz.devlib.common.MethodPair;

/**
 * Class相关工具类
 *
 * @author wangchongjie
 */
public class ClassUtilsInternal {

    public static Field getFieldInternal(String field, Class<?> clazz) {
        String cacheKey = clazz + "|" + field;
        Field result = null;
        try {
            result = clazz.getDeclaredField(field);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
        }
        try {
            result = clazz.getField(field);
            if (result != null) {
                return result;
            }
        } catch (Exception ex) {
        }
        // 递归获取父类的所有字段
        Class<?> superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            result = getFieldInternal(field, superClass);
            if (result != null) {
                return result;
            }
        } else {
            return null;
        }
        return null;
    }

    public static List<String> getAnnotationMarkedColumnsInternal(Class<?> clazz, Class<? extends Annotation>
            annotType) {
        List<String> columns = new ArrayList<String>();

        // 获取该实体类的解析结果
        Set<Field> fields = ClassUtils.getAllFiled(clazz);

        // 循环处理所有字段，过滤出该类加载为对象时需要调用的setter方法map
        for (Field f : fields) {
            // 静态字段则自动pass
            // if (Modifier.isStatic(f.getModifiers())) {
            // continue;
            // }
            Annotation[] annots = f.getAnnotations();
            // 无注解则pass
            if (ArrayUtils.isEmpty(annots)) {
                continue;
            }
            String columnName = f.getName();
            for (Annotation annot : annots) {
                if (annot.annotationType().equals(annotType)) {
                    columns.add(columnName);
                }
            }
        }
        return columns;
    }

    /**
     * 生成MethodPair
     *
     * @param clazz
     *
     * @return Map<String, MethodPair>
     */
    public static Map<String, MethodPair> getGStterMethodMapperInternal(Class<?> clazz) {

        // 获取该实体类的解析结果
        Set<Field> fields = ClassUtils.getAllFiled(clazz);
        Set<Method> methods = ClassUtils.getAllMethod(clazz);
        Map<String, Method> gsetterMap = filterGSetter2Map(methods);

        Map<String, MethodPair> methodMap = new HashMap<String, MethodPair>();

        // 循环处理所有字段，过滤出该类加载为对象时需要调用的setter方法map
        for (Field f : fields) {
            // 静态字段则自动pass
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            // 不做关联加载的工作
            Class<?> fType = f.getType();
            if (fType.getName().startsWith("com.baidu.") || Collection.class.isAssignableFrom(fType) || fType
                    .isArray()) {
                continue;
            }
            // 字段名字
            String name = f.getName();
            String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
            // 其他字段获取field，getter，setter
            String setter = "set" + upperName;
            Method set = gsetterMap.get(setter);

            String getter = "get" + upperName;
            Method get = gsetterMap.get(getter);
            if (get == null) {
                get = gsetterMap.get("is" + upperName);
            }
            if (get == null || set == null) {
                continue;
            }
            // 获取字段的注解，如果没有，则从getter或者setter上获取注解
            // OlapColumn column = OlapUtils.getColumnAnnotation(f, set, get);

            // 如果数据库映射字段不为空，则按照映射关系设置字段
            //            if (column == null) {
            methodMap.put(name, new MethodPair(set, get));
            //            } else if (column.value().equals("ignore")) {
            //            } else if (StringUtils.isEmpty(column.alias())) {
            //                methodMap.put(column.value(), new MethodPair(set, get));
            //            } else {
            //                methodMap.put(column.alias(), new MethodPair(set, get));
            //            }
        }
        return methodMap;
    }

    /**
     * 获取非static的public的getter、setter方法
     *
     * @param methods 方法集合
     *
     * @return Map<fieldName, Method>
     */
    private static Map<String, Method> filterGSetter2Map(Set<Method> methods) {

        Map<String, Method> map = new HashMap<String, Method>();
        Set<Method> sameNameSetters = new HashSet<Method>();
        for (Method m : methods) {
            boolean flag = Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers());
            if (flag) {

                String name = m.getName();
                if (name.startsWith("get") && m.getParameterTypes().length == 0) {
                } else if (name.startsWith("is") && m.getParameterTypes().length == 0) {
                } else if (name.startsWith("set") && m.getParameterTypes().length == 1) {
                } else if (name.equalsIgnoreCase("setUnixTime") // add by wangchongjie for OlapEngine
                        && m.getParameterTypes().length == 2) {
                } else {
                    continue;
                }

                // 获取同名的方法
                Method old = map.get(name);

                // 如果之前没有同名方法,则添加本方法
                if (old == null) {
                    map.put(name, m);

                    // 如果有同名方法，且本方法在子类中声明，且，父类本方法包含了annotation，则替换原来的方法
                    //                } else if (old.getDeclaringClass().isAssignableFrom(m.getDeclaringClass())
                    //                        && m.getAnnotation(OlapColumn.class) != null) {
                    //                    map.put(name, m);
                    // 重名setter存储备用
                } else if (name.startsWith("set")) {
                    sameNameSetters.add(m);
                }
            }
        }
        // 修正getter和setter类型不匹配
        for (Method setter : sameNameSetters) {
            String name = setter.getName();
            Method oldSetter = map.get(name);

            Method getter = map.get(name.replaceFirst("set", "get"));
            if (getter == null) {
                getter = map.get(name.replaceFirst("set", "is"));
            }
            if (getter == null) {
                continue;
            }
            // 如果原get、set方法的参数不匹配，则替换原来的方法
            if (!oldSetter.getParameterTypes()[0].equals(getter.getGenericReturnType())
                    && setter.getParameterTypes()[0].equals(getter.getGenericReturnType())) {
                map.put(name, setter);
                //            } else if (oldSetter.getDeclaringClass().isAssignableFrom(setter.getDeclaringClass())
                //                    && setter.getAnnotation(OlapColumn.class) != null) {
                //                map.put(name, setter);
            }
        }
        return map;
    }

}
