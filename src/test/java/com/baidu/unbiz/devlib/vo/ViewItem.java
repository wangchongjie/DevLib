package com.baidu.unbiz.devlib.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by wangchongjie on 16/3/25.
 */
public class ViewItem {
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

    public static List<ViewItem> mockList1() {
        List<ViewItem> list1 = new ArrayList<ViewItem>();
        ViewItem item1 = new ViewItem();
        item1.setId(1).setName("name1");
        ViewItem item2 = new ViewItem();
        item2.setId(2).setName("name2");
        ViewItem item3 = new ViewItem();
        item3.setId(3).setName("name3");
        list1.add(item2);
        list1.add(item3);
        list1.add(item1);
        return list1;
    }

    public static List<ViewItem> mockList2() {
        List<ViewItem> list2 = new ArrayList<ViewItem>();
        ViewItem item1 = new ViewItem();
        item1.setId(1).setCpc(1).setCpm(1).setCtr(1);
        ViewItem item2 = new ViewItem();
        item2.setId(2).setCpc(2).setCpm(2).setCtr(2);
        ViewItem item3 = new ViewItem();
        item3.setId(3).setCpc(3).setCpm(3).setCtr(3);
        ViewItem item4 = new ViewItem();
        item4.setId(4).setCpc(4).setCpm(4).setCtr(4);
        list2.add(item1);
        list2.add(item2);
        list2.add(item3);
        list2.add(item4);
        return list2;
    }
}
