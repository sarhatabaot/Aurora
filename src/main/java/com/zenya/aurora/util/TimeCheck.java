package com.zenya.aurora.util;

import com.zenya.aurora.storage.StorageFileManager;

public class TimeCheck {

    private static final long MIN_TIME = StorageFileManager.getConfig().getInt("start-spawning-at");
    private static final long MAX_TIME = StorageFileManager.getConfig().getInt("stop-spawning-at");

    public static long getRelTime(Long time) {
        return time % 24000;
    }

    public static boolean isDuring(Long time) {
        long relTime = getRelTime(time);

        if (MAX_TIME > MIN_TIME) {
            return (relTime > MIN_TIME && relTime < MAX_TIME);
        }

        if (MAX_TIME < MIN_TIME) {
            return (relTime > MIN_TIME || relTime < MAX_TIME);
        }

        return relTime == MIN_TIME;

    }
}
