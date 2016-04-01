package com.baidu.unbiz.devlib.collection;

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
public abstract class ToKvMapConverter<ITEM, KEY, VALUE> {

    private final static Log LOG = LogFactory.getLog(ToKvMapConverter.class);

    public Map<KEY, VALUE> convert(final List<ITEM> objs) {
        Map<KEY, VALUE> map = new HashMap<KEY, VALUE>();
        if (CollectionUtils.isEmpty(objs)) {
            return map;
        }
        for (ITEM obj : objs) {
            KEY key = this.getMapKey(obj);
            if (key != null) {
                map.put(key, this.getMapValue(obj));
            } else {
                LOG.warn("Null Key Obj:" + obj);
            }
        }
        return map;
    }

    public abstract KEY getMapKey(ITEM obj);

    public abstract VALUE getMapValue(ITEM obj);

}
