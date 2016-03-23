package com.baidu.unbiz.report.engine.toolbox.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 通用List转Map字典工具
 * 
 * @author wangchongjie
 * @fileName ToMapConverter.java
 * @dateTime 2015-3-9 下午1:10:39
 */
public abstract class ToMapConverter<N, T> {

    private final static Log LOG = LogFactory.getLog(ToMapConverter.class);

    public Map<N, T> convert(final List<T> objs) {
        Map<N, T> map = new HashMap<N, T>();
        if (CollectionUtils.isEmpty(objs)) {
            return map;
        }
        for (T obj : objs) {
            N key = this.getMapKey(obj);
            if (key != null) {
                map.put(key, obj);
            } else {
                LOG.warn("Null Key Obj:" + obj);
            }
        }
        return map;
    }

    public abstract N getMapKey(T obj);

}
