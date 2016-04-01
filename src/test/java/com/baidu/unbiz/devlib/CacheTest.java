package com.baidu.unbiz.devlib;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.springframework.util.Assert;

import com.baidu.unbiz.devlib.cache.AtomicComputeCache;
import com.baidu.unbiz.devlib.cache.Cache;
import com.baidu.unbiz.devlib.cache.decorators.SynchronizedCache;
import com.baidu.unbiz.devlib.cache.decorators.LoggingCache;
import com.baidu.unbiz.devlib.cache.decorators.LruCache;
import com.baidu.unbiz.devlib.cache.impl.PerpetualCache;
import com.baidu.unbiz.devlib.time.TimeRecoder;

public class CacheTest {

    @Test
    public void testDecoratorCache() {
        Cache cache = new SynchronizedCache(new LruCache(new LoggingCache(new PerpetualCache("test"))));

        cache.putObject("key", "value1");
        cache.putObject("key", "value2");
        Assert.state(("value2".equals(cache.getObject("key"))));
        System.out.print(cache.getObject("key"));
    }

    @Test
    public void testAtomicComputeCache() {
        AtomicComputeCache<String, Integer> cache = new AtomicComputeCache<String, Integer>();

        TimeRecoder timeRecoder = TimeRecoder.newTimeRecoder();
        Integer result = cache.getComputeResult("one", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 1;
            }
        });
        Assert.isTrue(result.equals(1));
        timeRecoder.record("first query");

        result = cache.getComputeResult("one", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 1;
            }
        });
        Assert.isTrue(result.equals(1));
        timeRecoder.record("second query");

        result = cache.getComputeResult("one", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 1;
            }
        });
        Assert.isTrue(result.equals(1));
        timeRecoder.record("third query");

        Integer result3 = cache.preGetAlreadyDoneResult("one");
        Assert.isTrue(result3.equals(1));

        Integer result2 = cache.preGetAlreadyDoneResult("two");
        Assert.isTrue(result2 == null);
        timeRecoder.record("pre query");
    }
}
