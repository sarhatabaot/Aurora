package com.zenya.aurora.util;

import org.bukkit.Bukkit;

public class CompatibilityHandler {

    public static String getVersion() {
        /*
        1.9.2 - v1_9_R1
        1.9.4 - v1_9_R2
        1.10.2 - v1_10_R1
        1.11.2 - v1_11_R1
        1.12.2 - v1_12_R1
        1.13 - v1_13_R1
        1.13.2 - v1_13_R2
        1.14.4 - v1_14_R1
        1.15.2 - v1_15_R1
        1.16.1 - v1_16_R1
        1.16.3 - v1_16_R2
        1.16.5 - v1_16_R3
         */
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static int getProtocol() {
        return Integer.parseInt(getVersion().split("_")[1]);
    }
}
