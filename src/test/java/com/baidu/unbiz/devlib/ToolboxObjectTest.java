package com.baidu.unbiz.devlib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.Assert;

import com.baidu.unbiz.devlib.collection.IdCollector;
import com.baidu.unbiz.devlib.vo.ViewItem;

public class ToolboxObjectTest {

    @Test
    public void testIdCollectorTest() {
        List<ViewItem> list = ViewItem.mockList1();
        IdCollector collector = new IdCollector<ViewItem>() {
        };
        Set<Integer> ids = collector.collectIds(list, "id");
        Set<String> names = collector.collectIds(list, "name");

        Assert.isTrue(ids.size() > 1);
        System.out.print(ids);
        Assert.isTrue(names.size() > 1);
        System.out.print(names);
    }

    @Test
    public void testElseIdCollectorTest() {
        List<ViewItem> list = ViewItem.mockList1();
        Set<Integer> idSet = new HashSet<Integer>();
        idSet.add(1);
        idSet.add(2);

        IdCollector collector = new IdCollector<ViewItem>() {
        };
        Assert.isTrue(collector.containIds(list, "id", idSet));

        collector.removeItemByIds(list, "id", idSet);
        Assert.isTrue(!collector.containIds(list, "id", idSet));
    }
}
