package com.raeic.embarker.reflections.classes;

import com.raeic.embarker.Globals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PacketPlayOutWorldBorderReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null &&
               getConstructor() != null &&
               getEnumWorldBorderAction() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getNMSClass("PacketPlayOutWorldBorder");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Constructor<?> getConstructor() {
        Class<?> reflectedClass = getReflectedClass();
        Class<?> EnumWorldBorderAction = getEnumWorldBorderAction();

        if (reflectedClass != null && EnumWorldBorderAction != null) {
            try {
                return reflectedClass.getConstructor(
                        Globals.reflectionManager.getWorldBorderReflection().getReflectedClass(),
                        EnumWorldBorderAction
                );
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Class<?> getEnumWorldBorderAction() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            return reflectedClass.getDeclaredClasses()[0];
        }
        return null;
    }

    public Object getEnumWorldBorderActionEnum(String enumValue) {
        try {
            return getEnumWorldBorderAction().getField(enumValue).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
