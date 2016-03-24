package com.baidu.unbiz.devlib.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.baidu.unbiz.devlib.common.MethodPair;
import com.baidu.unbiz.devlib.cache.AtomicComputeCache;

/**
 * Class相关工具类
 *
 * @author wangchongjie
 * @fileName ClassUtils.java
 * @dateTime 2014-7-16 下午3:02:20
 */
public class ClassUtils {

    private static AtomicComputeCache<Class<?>, List<String>> annotationMarkedColumnCache =
            new AtomicComputeCache<Class<?>, List<String>>();

    private static AtomicComputeCache<Class<?>, Map<String, MethodPair>> gstterMethodMapperCache =
            new AtomicComputeCache<Class<?>, Map<String, MethodPair>>();

    /**
     * key为clazz+fieldName,value为Field对象
     */
    private static AtomicComputeCache<String, Field> fieldCache = new AtomicComputeCache<String, Field>();


    /**
     * 遍历所有field（父子类、public、protected、private）
     *
     * @param clazz
     * @param field
     */
    public static Field getField(final String field, final Class<?> clazz) {
        String cacheKey = clazz + "|" + field;
        return fieldCache.getComputeResult(cacheKey, new Callable<Field>() {
            @Override
            public Field call() throws Exception {
                return ClassUtilsInternal.getFieldInternal(field, clazz);
            }
        });
    }

    /**
     * 获取注解标记的所有成员变量
     *
     * @param clazz
     * @return AnnotationMarkedColumns
     */
    public static List<String> getAnnotationMarkedColumns(final Class<?> clazz,
                                                          final Class<? extends Annotation> annotType) {
        return annotationMarkedColumnCache.getComputeResult(clazz, new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return ClassUtilsInternal.getAnnotationMarkedColumnsInternal(clazz, annotType);
            }
        });
    }

    public static Map<String, MethodPair> getGStterMethodMapper(final Class<?> clazz) {
        return gstterMethodMapperCache.getComputeResult(clazz, new Callable<Map<String, MethodPair>>() {
            @Override
            public Map<String, MethodPair> call() throws Exception {
                return ClassUtilsInternal.getGStterMethodMapperInternal(clazz);
            }
        });
    }

    /**
     * 获取某个类锁指定的泛型参数数组
     *
     * @param clazz class
     * @return Type[]
     */
    public final static Type[] getGenericTypes(Class<?> clazz) {
        Type superClass = clazz.getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) superClass;
        return type.getActualTypeArguments();
    }

    /**
     * 获取一个类的所有字段
     *
     * @param clazz class
     * @return Set<Field>
     */
    public static Set<Field> getAllFiled(Class<?> clazz) {

        // 获取本类的所有字段
        Set<Field> fs = new HashSet<Field>();
        for (Field f : clazz.getFields()) {
            fs.add(f);
        }
        for (Field f : clazz.getDeclaredFields()) {
            fs.add(f);
        }

        // 递归获取父类的所有字段
        Class<?> superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            Set<Field> superFileds = getAllFiled(superClass);
            fs.addAll(superFileds);
        }

        return fs;
    }

    /**
     * 获取一个类的所有方法
     *
     * @param clazz class
     * @return Set<Method>
     */
    public static Set<Method> getAllMethod(Class<?> clazz) {

        // 获取本类的所有的方法
        Set<Method> ms = new HashSet<Method>();
        for (Method m : clazz.getMethods()) {
            ms.add(m);
        }
        for (Method m : clazz.getDeclaredMethods()) {
            ms.add(m);
        }

        // 递归获取父类的所有方法
        Class<?> superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            Set<Method> superFileds = getAllMethod(superClass);
            ms.addAll(superFileds);
        }

        return ms;
    }

    /**
     * 将from的属性copy到to中
     *
     * @param from
     * @param to
     */
    public final static void copyProperties(Object from, Object to) {

        Set<Field> fromSet = getAllFiled(from.getClass());
        Set<Field> toSet = getAllFiled(to.getClass());

        Map<String, Field> toMap = new HashMap<String, Field>();
        for (Field f : toSet) {
            toMap.put(f.getName(), f);
        }

        for (Field f : fromSet) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            String name = f.getName();
            Field toField = toMap.get(name);
            if (toField == null) {
                continue;
            }

            toField.setAccessible(true);
            f.setAccessible(true);
            try {
                toField.set(to, f.get(from));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取一个类的field
     *
     * @param field
     * @param clazz
     * @return Field
     */
    public static Field getFieldFromClass(String field, Class<? extends Object> clazz) {
        try {
            return clazz.getDeclaredField(field);
        } catch (Exception e) {
            try {
                return clazz.getField(field);
            } catch (Exception ex) {
            }
        }
        return null;
    }

    /**
     * 获取某个字段的annotation，从继承链最下面获取
     *
     * @param field
     * @param set
     * @param get
     * @return annotation
     */
    public static <ANNOT> ANNOT getColumnAnnotation(Field field, Method set, Method get, Class annotCLazz) {

        // 三个地方都有可能出现column
        ANNOT column = (ANNOT) field.getAnnotation(annotCLazz);
        ANNOT gColumn = (ANNOT) get.getAnnotation(annotCLazz);
        ANNOT sColumn = (ANNOT) set.getAnnotation(annotCLazz);

        // 预先获取出get与set所在的类
        Class<?> sClass = set.getDeclaringClass();
        Class<?> gClass = get.getDeclaringClass();

        // 如果get上定义了annotation，且get定义的地方是子类
        if (gColumn != null && !gClass.isAssignableFrom(sClass)) {
            return gColumn;
        }

        // 如果是set上定义的annotation，且set方法不在父类中定义
        if (sColumn != null && !sClass.isAssignableFrom(gClass)) {
            return sColumn;
        }

        return column;
    }

}
