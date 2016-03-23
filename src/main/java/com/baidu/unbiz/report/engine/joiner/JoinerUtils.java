package com.baidu.unbiz.report.engine.joiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SortOrder;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import com.baidu.unbiz.report.engine.log.AopLogFactory;

/**
 * Created by wangchongjie on 16/3/23.
 */
public class JoinerUtils {

    protected static final Logger LOG = AopLogFactory.getLogger(JoinerUtils.class);

    /**
     * Item列表merge
     *
     * @param mainList
     * @param subList
     * @param mergeKey
     * @param mergeVal
     * @param clazz
     * @param appendDisMatchItem
     *
     * @return
     *
     * @since 2015-7-28 by wangchongjie
     */
    public static <T> List<T> joinItemList(List<T> mainList, List<T> subList, String mergeKey,
                                           String mergeVal, Class<?> clazz, boolean appendDisMatchItem) {

        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add(mergeKey);
        Set<String> mergeVals = new HashSet<String>();
        mergeVals.add(mergeVal);
        return joinItemList(mainList, subList, mergeKeys, mergeVals, clazz, SortOrder.DESCENDING, appendDisMatchItem);
    }

    /**
     * Item列表merge
     *
     * @param mainList
     * @param subList
     * @param mergeKey
     * @param mergeVals
     * @param clazz
     * @param appendDisMatchItem
     *
     * @return
     *
     * @since 2015-7-28 by wangchongjie
     */
    public static <T> List<T> joinItemList(List<T> mainList, List<T> subList, String mergeKey,
                                           Set<String> mergeVals, Class<?> clazz, boolean appendDisMatchItem) {

        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add(mergeKey);
        return joinItemList(mainList, subList, mergeKeys, mergeVals, clazz, SortOrder.DESCENDING, appendDisMatchItem);
    }

    /**
     * Item列表merge
     *
     * @param mainList
     * @param subList
     * @param mergeKeys
     * @param mergeVals
     * @param clazz
     * @param appendDisMatchItem
     *
     * @return
     *
     * @since 2015-7-28 by wangchongjie
     */
    public static <T> List<T> joinItemList(List<T> mainList, List<T> subList, Set<String> mergeKeys,
                                           Set<String> mergeVals, Class<?> clazz, boolean appendDisMatchItem) {

        return joinItemList(mainList, subList, mergeKeys, mergeVals, clazz, SortOrder.DESCENDING, appendDisMatchItem);
    }

    /**
     * Code generator方式实现，效率比 mergeItemList的反射实现方式高 将两个list按指定keys字段merger成一个list;
     * 按mergeKeys为keys，将subList的mergeVals，merge到mainList中; 若appendDisMatchItem为true，则将不匹配的记录也添加到结果集中;
     * order为倒叙则不匹配的结果集将追加到result的末尾，正顺则放开头;
     *
     * @param mainList
     * @param subList
     * @param mergeKeys
     * @param mergeVals
     * @param clazz
     * @param order
     * @param appendDisMatchItem
     *
     * @return
     *
     * @throws Exception 2014-12-17 下午5:17:34 created by wangchongjie
     */
    public static <T> List<T> joinItemList(List<T> mainList, List<T> subList, Set<String> mergeKeys,
                                           Set<String> mergeVals, Class<?> clazz, SortOrder order,
                                           boolean appendDisMatchItem) {
        if (CollectionUtils.isEmpty(mergeKeys) || CollectionUtils.isEmpty(mergeVals) || clazz == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(mainList) && CollectionUtils.isEmpty(subList)) {
            return new ArrayList<T>();
        }
        if (CollectionUtils.isEmpty(mainList)) {
            if (appendDisMatchItem) {
                return new ArrayList<T>(subList);
            } else {
                return new ArrayList<T>();
            }
        } else if (CollectionUtils.isEmpty(subList)) {
            return new ArrayList<T>(mainList);
        }
        List<T> finalList = new ArrayList<T>(mainList);
        Map<String, T> itemMap = new HashMap<String, T>();

        ItemJoinerSupport joiner = JoinerCodeGenerator.getOlapJoinerSupportClass(mergeKeys, mergeVals, clazz);

        try {
            for (T item : subList) {
                String multiKey = joiner.getKeys(item);
                itemMap.put(multiKey, item);
            }
            for (T item : finalList) {
                String multiKey = joiner.getKeys(item);
                T subItem = itemMap.get(multiKey);
                if (subItem != null) {
                    joiner.setValues(subItem, item);
                    itemMap.remove(multiKey);
                }
            }
            if (appendDisMatchItem) {
                if (order == SortOrder.DESCENDING) {
                    finalList.addAll(itemMap.values());
                }
                if (order == SortOrder.ASCENDING) {
                    List<T> tmpList = new ArrayList<T>(itemMap.values());
                    tmpList.addAll(finalList);
                    finalList = tmpList;
                }
            }
        } catch (IllegalArgumentException e) {
            LOG.error("join fail: ", e);
        }

        return finalList;
    }

    /**
     * Item列表merge
     *
     * @param mainList
     * @param subList
     * @param mergeKey
     * @param mergeVal
     * @param clazz
     * @param order
     * @param appendDisMatchItem
     * @param keepMainListOrder
     *
     * @return
     *
     * @since 2015-7-28 by wangchongjie
     */
    public static <T> List<T> joinItemListKeepOrder(List<T> mainList, List<T> subList,
                                                    String mergeKey, String mergeVal, Class<?> clazz, SortOrder order,
                                                    boolean appendDisMatchItem, boolean keepMainListOrder) {

        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add(mergeKey);
        Set<String> mergeVals = new HashSet<String>();
        mergeVals.add(mergeVal);
        return joinItemListKeepOrder(mainList, subList, mergeKeys, mergeVals, clazz, order, appendDisMatchItem,
                keepMainListOrder);
    }

    /**
     * Item列表merge
     *
     * @param mainList
     * @param subList
     * @param mergeKey
     * @param mergeVals
     * @param clazz
     * @param order
     * @param appendDisMatchItem
     * @param keepMainListOrder
     *
     * @return
     *
     * @since 2015-7-28 by wangchongjie
     */
    public static <T> List<T> joinItemListKeepOrder(List<T> mainList, List<T> subList,
                                                    String mergeKey, Set<String> mergeVals, Class<?> clazz,
                                                    SortOrder order,
                                                    boolean appendDisMatchItem, boolean keepMainListOrder) {

        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add(mergeKey);
        return joinItemListKeepOrder(mainList, subList, mergeKeys, mergeVals, clazz, order, appendDisMatchItem,
                keepMainListOrder);
    }

    /**
     * Code generator方式实现，效率比 mergeItemListKeepOrder的反射实现方式高 保序merge，
     * keepMainListOrder可控制以哪个list的顺序为准
     *
     * @param mainList
     * @param subList
     * @param mergeKey
     * @param mergeVals
     * @param clazz
     * @param order
     * @param appendDisMatchItem
     * @param keepMainListOrder
     *
     * @return 2014-12-17 下午5:34:02 created by wangchongjie
     */
    public static <T> List<T> joinItemListKeepOrder(List<T> mainList, List<T> subList,
                                                    Set<String> mergeKey, Set<String> mergeVals, Class<?> clazz,
                                                    SortOrder order,
                                                    boolean appendDisMatchItem, boolean keepMainListOrder) {

        if (keepMainListOrder) {
            return joinItemList(mainList, subList, mergeKey, mergeVals, clazz, order, appendDisMatchItem);
        } else {
            return joinItemListKeepSubListOrder(mainList, subList, mergeKey, mergeVals, clazz, order,
                    appendDisMatchItem);
        }
    }

    /**
     * Code generator方式实现，效率比 mergeItemList的反射实现方式高 保持sublist的顺序的方式merge
     *
     * @param mainList
     * @param subList
     * @param mergeKeys
     * @param mergeVals
     * @param clazz
     * @param order
     * @param appendDisMatchItem
     *
     * @return 2014-12-17 下午5:26:07 created by wangchongjie
     */
    public static <T> List<T> joinItemListKeepSubListOrder(List<T> mainList, List<T> subList,
                                                           Set<String> mergeKeys, Set<String> mergeVals, Class<?> clazz,
                                                           SortOrder order,
                                                           boolean appendDisMatchItem) {
        if (CollectionUtils.isEmpty(mergeKeys) || CollectionUtils.isEmpty(mergeVals) || clazz == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(mainList) && CollectionUtils.isEmpty(subList)) {
            return new ArrayList<T>();
        }
        if (CollectionUtils.isEmpty(mainList)) {
            return new ArrayList<T>(subList);
        } else if (CollectionUtils.isEmpty(subList)) {
            return new ArrayList<T>(mainList);
        }

        List<T> finalList = new ArrayList<T>();
        Map<String, T> itemMap = new HashMap<String, T>();
        ItemJoinerSupport joiner = JoinerCodeGenerator.getOlapJoinerSupportClass(mergeKeys, mergeVals, clazz);

        try {
            // 将主表处理成key为联合key，value为次表item的map，供join使用
            for (T item : mainList) {
                String multiKey = joiner.getKeys(item);
                itemMap.put(multiKey, item);
            }

            // 向主表的item中填充此表的value字段
            for (T item : subList) {
                String multiKey = joiner.getKeys(item);
                T mainItem = itemMap.get(multiKey);
                if (mainItem != null) {
                    // 逐一字段merge
                    joiner.setValues(item, mainItem);
                    finalList.add(mainItem);
                    itemMap.remove(multiKey);
                } else {
                    finalList.add(item);
                }
            }
            if (appendDisMatchItem) {
                if (order == SortOrder.DESCENDING) {
                    finalList.addAll(itemMap.values());
                }
                if (order == SortOrder.ASCENDING) {
                    List<T> tmpList = new ArrayList<T>(itemMap.values());
                    tmpList.addAll(finalList);
                    finalList = tmpList;
                }
            }
        } catch (IllegalArgumentException e) {
            LOG.error("join fail: ", e);
        }
        return finalList;
    }

}
