package com.baidu.unbiz.devlib.joiner;

/**
 * Joiner辅助接口
 * 
 * @author wangchongjie
 */
public interface ItemJoinerSupport {

    /**
     * 获取merge key
     * 
     * @param obj
     * @return keys
     */
    String getKeys(Object obj);

    /**
     * source数据merge到target中
     * 
     * @param source soucrce item
     * @param target target item
     */
    void setValues(Object source, Object target);
}
