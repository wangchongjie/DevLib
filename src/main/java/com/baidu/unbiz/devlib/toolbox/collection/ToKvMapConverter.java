package com.baidu.unbiz.devlib.toolbox.collection;

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
public abstract class ToKvMapConverter<T, K, V> {

    private final static Log LOG = LogFactory.getLog(ToKvMapConverter.class);

    public Map<K, V> convert(final List<T> objs) {
        Map<K, V> map = new HashMap<K, V>();
        if (CollectionUtils.isEmpty(objs)) {
            return map;
        }
        for (T obj : objs) {
            K key = this.getMapKey(obj);
            if (key != null) {
                map.put(key, this.getMapValue(obj));
            } else {
                LOG.warn("Null Key Obj:" + obj);
            }
        }
        return map;
    }

    public abstract K getMapKey(T obj);

    public abstract V getMapValue(T obj);

}
