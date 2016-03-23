package com.baidu.unbiz.report.engine.common;

import java.lang.reflect.Method;

/**
 * 一个get与set方法的配对
 */
public class MethodPair {

    /**
     * 构造方法
     * 
     * @param setterMethod setterMethod
     * @param getterMethod getterMethod
     */
    public MethodPair(Method setterMethod, Method getterMethod) {
        this.getter = getterMethod;
        this.setter = setterMethod;
    }

    /**
     * getter
     */
    public Method getter;
    
    /**
     * setter
     */
    public Method setter;
}