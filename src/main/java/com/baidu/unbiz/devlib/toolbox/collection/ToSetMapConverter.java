package com.baidu.unbiz.devlib.toolbox.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 通用List转Set Map字典工具
 * 
 * @author wangchongjie
 * @fileName ToMapConverter.java
 * @dateTime 2015-3-9 下午1:10:39
 */
public abstract class ToSetMapConverter<T, N, M> {

    private final static Log LOG = LogFactory.getLog(ToSetMapConverter.class);

    public Map<N, Set<M>> convert(final List<T> objs) {
        Map<N, Set<M>> map = new HashMap<N, Set<M>>();
        if (CollectionUtils.isEmpty(objs)) {
            return map;
        }
        for (T obj : objs) {
            N key = this.getMapKey(obj);
            if (key != null) {
                Set<M> set = map.get(key);
                if (set == null) {
                    set = new HashSet<M>();
                    map.put(key, set);
                }
                set.add(this.getSetValue(obj));
            } else {
                LOG.warn("Null Key Obj:" + obj);
            }
        }
        return map;
    }

    public abstract N getMapKey(T obj);

    public abstract M getSetValue(T obj);
}
