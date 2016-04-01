package com.baidu.unbiz.devlib.collection;

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
public abstract class ToSetMapConverter<ITEM, KEY, VALUE> {

    private final static Log LOG = LogFactory.getLog(ToSetMapConverter.class);

    public Map<KEY, Set<VALUE>> convert(final List<ITEM> objs) {
        Map<KEY, Set<VALUE>> map = new HashMap<KEY, Set<VALUE>>();
        if (CollectionUtils.isEmpty(objs)) {
            return map;
        }
        for (ITEM obj : objs) {
            KEY key = this.getMapKey(obj);
            if (key != null) {
                Set<VALUE> set = map.get(key);
                if (set == null) {
                    set = new HashSet<VALUE>();
                    map.put(key, set);
                }
                set.add(this.getSetValue(obj));
            } else {
                LOG.warn("Null Key Obj:" + obj);
            }
        }
        return map;
    }

    public abstract KEY getMapKey(ITEM obj);

    public abstract VALUE getSetValue(ITEM obj);
}
