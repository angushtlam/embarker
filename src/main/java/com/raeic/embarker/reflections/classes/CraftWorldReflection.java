package com.raeic.embarker.reflections.classes;

import com.raeic.embarker.Globals;

import java.lang.reflect.Method;

public class CraftWorldReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null && getHandleMethod() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getCraftBukkitClass("CraftWorld");
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
