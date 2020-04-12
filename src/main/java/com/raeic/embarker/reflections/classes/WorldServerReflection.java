package com.raeic.embarker.reflections.classes;

import com.raeic.embarker.Globals;

import java.lang.reflect.Method;

public class WorldServerReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getNMSClass("WorldServer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
