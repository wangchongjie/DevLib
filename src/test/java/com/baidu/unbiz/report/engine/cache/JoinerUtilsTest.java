package com.baidu.unbiz.report.engine.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;
import org.springframework.util.Assert;

import com.baidu.unbiz.report.engine.cache.decorators.LoggingCache;
import com.baidu.unbiz.report.engine.cache.decorators.LruCache;
import com.baidu.unbiz.report.engine.cache.decorators.SynchronizedCache;
import com.baidu.unbiz.report.engine.cache.impl.PerpetualCache;
import com.baidu.unbiz.report.engine.joiner.JoinerUtils;
import com.baidu.unbiz.report.engine.toolbox.time.TimeRecoder;

public class JoinerUtilsTest {

    @Test
    public void testJoinerUtils() {
        List<ViewItem> list1 = this.mockList1();
        List<ViewItem> list2 = this.mockList2();
        Set<String> mergeKeys = new HashSet<String>();
        mergeKeys.add("id");
        Set<String> mergeVals = new HashSet<String>();
        mergeVals.add("cpm");
        mergeVals.add("cpc");
        mergeVals.add("ctr");
        JoinerUtils.joinItemList(list1, list2, mergeKeys, mergeVals, ViewItem.class, true);
        System.out.print(list1);
    }

    private List<ViewItem> mockList1() {
        List<ViewItem> list1 = new ArrayList<ViewItem>();
        ViewItem item1 = new ViewItem();
        item1.setId(1).setName("name1");
        ViewItem item2 = new ViewItem();
        item1.setId(2).setName("name2");
        ViewItem item3 = new ViewItem();
        item1.setId(3).setName("name3");
        list1.add(item1);
        list1.add(item2);
        list1.add(item3);
        return list1;
    }

    private List<ViewItem> mockList2() {
        List<ViewItem> list1 = new ArrayList<ViewItem>();
        ViewItem item1 = new ViewItem();
        item1.setId(1).setCpc(1).setCpm(1).setCpc(1);
        ViewItem item2 = new ViewItem();
        item1.setId(2).setCpc(2).setCpm(2).setCpc(2);
        ViewItem item3 = new ViewItem();
        item1.setId(3).setCpc(3).setCpm(3).setCpc(3);
        list1.add(item1);
        list1.add(item2);
        list1.add(item3);
        return list1;
    }

    class ViewItem {
        private int id;
        private String name;
        private double cpm;
        private double ctr;
        private double cpc;

        public double getCpc() {
            return cpc;
        }

        public ViewItem setCpc(double cpc) {
            this.cpc = cpc;
            return this;
        }

        public int getId() {
            return id;
        }

        public ViewItem setId(int id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public ViewItem setName(String name) {
            this.name = name;
            return this;
        }

        public double getCpm() {
            return cpm;
        }

        public ViewItem setCpm(double cpm) {
            this.cpm = cpm;
            return this;
        }

        public double getCtr() {
            return ctr;
        }

        public ViewItem setCtr(double ctr) {
            this.ctr = ctr;
            return this;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
