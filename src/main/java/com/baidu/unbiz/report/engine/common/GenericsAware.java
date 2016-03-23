package com.baidu.unbiz.report.engine.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.baidu.unbiz.report.engine.util.ClassUtils;


/**
 * 通过new<T>的方式可以感知范型，也可自行传入
 */
public abstract class GenericsAware<T> {
    protected Class<T> entityClass;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public GenericsAware() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (!params[0].toString().equals("T")) {
            entityClass = (Class) params[0];
        }
    }
    
    /**
     * 获取该类的某个成员变量
     * 
     * @param field
     * @return Field
     */
    public Field getFieldFromClass(String field) {
        Class<?> clazz = this.getItemClazz();
        if (clazz == null) {
            throw new RuntimeException("entity clazz is null");
        }
        return ClassUtils.getCachedFieldFromClass(field, clazz);
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @SuppressWarnings({ "rawtypes" })
    public Class getItemClazz() {
        return this.entityClass;
    }
}
