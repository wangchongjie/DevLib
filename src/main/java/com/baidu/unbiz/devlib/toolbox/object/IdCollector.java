package com.baidu.unbiz.devlib.toolbox.object;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.devlib.common.GenericsAware;

/**
 * 通用IDs收集器
 * 
 * @author wangchongjie
 * @fileName IdCollector.java
 * @dateTime 2014-11-21 下午7:57:08
 */
public abstract class IdCollector<T> extends GenericsAware<T> {

    private final static Log LOG = LogFactory.getLog(IdCollector.class);

    @SuppressWarnings("unchecked")
    public <N> Set<N> collectIds(Collection<T> itemList, String fieldName) {
        Set<N> result = new HashSet<N>();
        if (CollectionUtils.isEmpty(itemList)) {
            return result;
        }
        Field field = getFieldFromClass(fieldName);
        if (field == null) {
            return result;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        for (T item : itemList) {
            try {
                N target = (N) field.get(item);
                if (target != null) {
                    result.add(target);
                }
            } catch (IllegalArgumentException e) {
                LOG.error("IllegalArgumentException for collect IDs", e);
            } catch (IllegalAccessException e) {
                LOG.error("IllegalAccessException for collect IDs", e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <N> boolean containIds(List<T> itemList, String fieldName, Collection<N> idSet) {
        if (CollectionUtils.isEmpty(itemList)) {
            return false;
        }
        Field field = getFieldFromClass(fieldName);
        if (field == null) {
            return false;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        for (T item : itemList) {
            try {
                N target = (N) field.get(item);
                if (target != null && idSet.contains(target)) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                LOG.error("IllegalArgumentException for collect IDs", e);
            } catch (IllegalAccessException e) {
                LOG.error("IllegalAccessException for collect IDs", e);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <N> boolean removeItemByIds(List<T> itemList, String fieldName, Collection<N> idSet) {
        if (CollectionUtils.isEmpty(itemList)) {
            return false;
        }
        Field field = getFieldFromClass(fieldName);
        if (field == null) {
            return false;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        Iterator<T> iter = itemList.iterator();

        boolean containIds = false;
        while (iter.hasNext()) {
            T item = iter.next();
            try {
                N target = (N) field.get(item);
                if (target != null && idSet.contains(target)) {
                    iter.remove();
                    containIds = true;
                }
            } catch (IllegalArgumentException e) {
                LOG.error("IllegalArgumentException for collect IDs", e);
            } catch (IllegalAccessException e) {
                LOG.error("IllegalAccessException for collect IDs", e);
            }
        }
        return containIds;
    }

    public static <T> IdCollector<T> build(Class<T> clazz) {
        IdCollector<T> collector = new IdCollector<T>() {
        };
        collector.setEntityClass(clazz);
        return collector;
    }
}
