package com.baidu.unbiz.report.engine.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 原子性计算缓存，同类计算可保证计算且只计算一次 线程安全，
 * 客户端无需加冗余同步机制、自旋等待等。计算Task会被临时缓存，计算结果被缓存后，会清除Task缓存
 *
 * @param <K> key类型
 * @param <V> value类型
 * @author wangchongjie
 */
public class AtomicComputeCache<K, V> {

    private ConcurrentMap<K, ComputableTask<K>> computingCache;
    private ConcurrentMap<K, V> computeResultCache;

    /**
     * 构造方法
     */
    public AtomicComputeCache() {
        this.computingCache = new ConcurrentHashMap<K, ComputableTask<K>>();
        this.computeResultCache = new ConcurrentHashMap<K, V>();
    }

    /**
     * 构造方法
     * 
     * @param initialCapacity 初始化大小
     */
    public AtomicComputeCache(int initialCapacity) {
        this.computingCache = new ConcurrentHashMap<K, ComputableTask<K>>(initialCapacity);
        this.computeResultCache = new ConcurrentHashMap<K, V>(initialCapacity);
    }

    /**
     * 计算耗时类型的任务
     */
    private static class ComputableTask<K> extends FutureTask<Object> {
        public ComputableTask(Callable<Object> callable) {
            super(callable);
        }

        public Object getValue(K key, ConcurrentMap<K, ComputableTask<K>> computingCache) {
            try {
                return get();
            } catch (InterruptedException e) {
                computingCache.remove(key);
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                computingCache.remove(key);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * get compute result whether the compute is done or not, 
     * thread will be block until computing done, the compute
     * result will be cached in JVM
     * 
     * @param key cahce key
     * @param biz computing logic
     * @return result
     */
    @SuppressWarnings("unchecked")
    public V getComputeResult(K key, Callable<V> biz) {
        if (computeResultCache.containsKey(key)) {
            return computeResultCache.get(key);
        }
        if (computingCache.containsKey(key)) {
            biz = null; // let gc
            ComputableTask<K> task = computingCache.get(key);
            if (task == null) { // other thread has done and swap
                return computeResultCache.get(key);
            }
            V result = (V) task.getValue(key, computingCache);
            this.swapResultAndCleanComputeTask(key, result);
            return result;
        } else {
            ComputableTask<K> task = new ComputableTask<K>((Callable<Object>)biz);
            ComputableTask<K> old = computingCache.putIfAbsent(key, task);
            if (old == null) { // success to put compute cache
                if (computeResultCache.containsKey(key)) { // other thread already done
                    V result = computeResultCache.get(key);
                    this.swapResultAndCleanComputeTask(key, result);
                    return result;
                }
                task.run();
            } else { // fail to put compute cache
                task = old;
            }
            V result = (V) task.getValue(key, computingCache);
            this.swapResultAndCleanComputeTask(key, result);
            return result;
        }
    }

    /**
     * pre get already done result 
     * use this method could avoid new Callable object per time 
     * warning: may not get the computing result
     * 
     * @param key
     * @return result
     */
    public V preGetAlreadyDoneResult(K key) {
        return computeResultCache.get(key);
    }

    private void swapResultAndCleanComputeTask(K key, V result) {
        computeResultCache.put(key, result);
        computingCache.remove(key);
    }

}
