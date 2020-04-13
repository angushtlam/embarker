package com.raeic.embarker.reflections.classes;

import com.raeic.embarker.Globals;

import java.lang.reflect.Field;

public class EntityPlayerReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null &&
               getPlayerConnectionField() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getNMSClass("EntityPlayer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Field getPlayerConnectionField() {
        Class<?> reflectedClass = getReflectedClass();

        if (reflectedClass != null) {
            try {
                return reflectedClass.getDeclaredField("playerConnection");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
