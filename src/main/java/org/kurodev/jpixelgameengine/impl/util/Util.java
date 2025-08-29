package org.kurodev.jpixelgameengine.impl.util;

import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

public class Util {
    public static String cString(MemorySegment ptr) {
        if (ptr == MemorySegment.NULL) return null;
        return ptr.reinterpret(Integer.MAX_VALUE).getString(0, StandardCharsets.UTF_8);
    }
}
