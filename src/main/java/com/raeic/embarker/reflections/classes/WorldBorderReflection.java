package com.raeic.embarker.reflections.classes;

import com.raeic.embarker.Globals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WorldBorderReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null &&
               getConstructor() != null &&
               setCenterMethod() != null &&
               setSizeMethod() != null &&
               getWorldField() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getNMSClass("WorldBorder");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Constructor<?> getConstructor() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            try {
                return reflectedClass.getConstructor();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Method setCenterMethod() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            try {
                return reflectedClass.getMethod("setCenter", double.class, double.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Method setSizeMethod() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            try {
                return reflectedClass.getMethod("setSize", double.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Field getWorldField() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            try {
                return reflectedClass.getDeclaredField("world");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
