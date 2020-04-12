package com.raeic.embarker.utils;

import org.bukkit.Bukkit;

public class ReflectionUtil {
    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                                 + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit."
                                 + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
