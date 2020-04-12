package com.raeic.embarker.reflections.classes;

import com.raeic.embarker.Globals;

import java.lang.reflect.Method;

public class PlayerConnectionReflection {
    public boolean isReflectionReady() {
        return getReflectedClass() != null && sendPacketMethod() != null;
    }

    public Class<?> getReflectedClass() {
        try {
            return Globals.reflectionManager.getNMSClass("PlayerConnection");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Method sendPacketMethod() {
        Class<?> reflectedClass = getReflectedClass();
        Class<?> Packet = Globals.reflectionManager.getPacketReflection().getReflectedClass();

        if (reflectedClass != null && Packet != null) {
            try {
                return reflectedClass.getMethod("sendPacket", Packet);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
