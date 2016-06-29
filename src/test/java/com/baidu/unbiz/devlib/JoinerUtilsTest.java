package com.baidu.unbiz.devlib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.Assert;

import com.baidu.unbiz.devlib.joiner.Joiner;
import com.baidu.unbiz.devlib.joiner.JoinerUtils;
import com.baidu.unbiz.devlib.vo.ViewItem;

public class JoinerUtilsTest {

    @Test
    public void testJoiner() {
        List<ViewItem> list1 = ViewItem.mockList1();
        List<ViewItem> list2 = ViewItem.mockList2();
        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add("id");
        Set<String> mergeVals = new HashSet<String>();
        mergeVals.add("cpm");
        mergeVals.add("cpc");
        mergeVals.add("ctr");

        List<ViewItem> result =
                Joiner.newJoiner(ViewItem.class).mergeKeys(mergeKeys).mergeVals(mergeVals).join(list1, list2);
        System.out.print(result);
    }

    @Test
    public void testJoinerWithOrderAndDisMatch() {
        List<ViewItem> list1 = ViewItem.mockList1();
        List<ViewItem> list2 = ViewItem.mockList2();
        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add("id");
        Set<String> mergeVals = new HashSet<String>();
        mergeVals.add("cpm");
        mergeVals.add("cpc");
        mergeVals.add("ctr");

        List<ViewItem> result =
                Joiner.newJoiner(ViewItem.class).mergeKeys(mergeKeys).mergeVals(mergeVals).appendDisMatchItem(true)
                        .keepMainListOrder(true).disMatchAppendTail(false).join(list1, list2);

        Assert.state(result.size() == 4);
        Assert.state(result.get(0).getId() == 4);
        System.out.print(result);
    }

    @Test
    public void testJoinerUtils() {
        List<ViewItem> list1 = ViewItem.mockList1();
        List<ViewItem> list2 = ViewItem.mockList2();
        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add("id");
        Set<String> mergeVals = new HashSet<String>();
        mergeVals.add("cpm");
        mergeVals.add("cpc");
        mergeVals.add("ctr");
        List<ViewItem> result = JoinerUtils.joinItemList(list1, list2, mergeKeys, mergeVals, ViewItem.class, true);
        System.out.print(result);
    }

}
