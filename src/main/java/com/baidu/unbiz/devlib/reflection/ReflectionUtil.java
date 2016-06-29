package com.baidu.unbiz.devlib.reflection;

import java.lang.reflect.Constructor;

import org.apache.commons.lang.ArrayUtils;

/**
 * Created by wangchongjie on 16/4/5.
 */
public class ReflectionUtil {
    public static <T> T newInstance(Class<T> clazz) {
        Constructor constructor = getDefaultConstructorsOfClass(clazz);
        if (constructor == null) {
            return null;
        } else {
            try {
                Object e = constructor.newInstance();
                return (T) e;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static Constructor<?> getDefaultConstructorsOfClass(Class<?> clazz) {

        Constructor<?>[] constructors = getAllConstructorsOfClass(clazz);
        if (ArrayUtils.isEmpty(constructors)) {
            return null;
        }
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor;
            }
        }
        return null;
    }

    public static Constructor<?>[] getAllConstructorsOfClass(Class<?> clazz) {
        return clazz == null ? null : clazz.getDeclaredConstructors();
    }
}
