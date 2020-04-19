package com.raeic.embarker.reflections.classes;

import java.lang.reflect.Method;

import com.raeic.embarker.Globals;

public class CraftPlayerReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null && getHandleMethod() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getCraftBukkitClass("entity.CraftPlayer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Method getHandleMethod() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            try {
                return reflectedClass.getMethod("getHandle");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
