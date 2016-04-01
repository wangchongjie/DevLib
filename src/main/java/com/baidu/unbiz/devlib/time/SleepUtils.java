/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the BAIDU
 */
package com.baidu.unbiz.devlib.time;

/**
 * Utility class for {@link Thread#sleep(long)}
 * 
 * @author xiemalin
 * @since 1.0.0
 */
public class SleepUtils {

    /**
     * Dummy  Causes the currently executing thread to sleep by {@link Thread#sleep(long)}
     * @param time time to sleep in MS
     */
    public static void dummySleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            // dummy all exception
        }
    }
}
