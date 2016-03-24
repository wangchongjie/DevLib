package com.baidu.unbiz.devlib.joiner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SortOrder;

/**
 * @author wangchongjie
 */
public class Joiner {

    private Class<?> clazz;
    private Set<String> mergeKeys;
    private Set<String> mergeVals;
    private boolean appendDisMatchItem = false;
    private boolean disMatchAppendTail = true;
    private boolean keepMainListOrder = true;

    private Joiner() {
    }

    private Joiner(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static Joiner newJoiner(Class<?> clazz) {
        return new Joiner(clazz);
    }

    public <T> List<T> join(List<T> mainList, List<T> subList) {
        return JoinerUtils.joinItemListKeepOrder(mainList, subList, mergeKeys, mergeVals, clazz, appendDisMatchItem,
                disMatchAppendTail, keepMainListOrder);
    }

    public Joiner appendDisMatchItem(boolean appendDisMatchItem) {
        this.appendDisMatchItem = appendDisMatchItem;
        return this;
    }

    public Joiner keepMainListOrder(boolean keepMainListOrder) {
        this.keepMainListOrder = keepMainListOrder;
        return this;
    }

    public Joiner mergeKeys(Set<String> mergeKeys) {
        this.mergeKeys = mergeKeys;
        return this;
    }

    public Joiner mergeVals(Set<String> mergeVals) {
        this.mergeVals = mergeVals;
        return this;
    }

    public Joiner mergeKey(String mergeKey) {
        if(this.mergeKeys == null) {
            this.mergeKeys = new HashSet<String>();
        }
        mergeKeys.add(mergeKey);
        return this;
    }

    public Joiner mergeVal(String mergeVal) {
        if(this.mergeVals == null) {
            this.mergeVals = new HashSet<String>();
        }
        mergeVals.add(mergeVal);
        return this;
    }

    public Joiner disMatchAppendTail(boolean disMatchAppendTail) {
        this.disMatchAppendTail = disMatchAppendTail;
        return this;
    }
}
