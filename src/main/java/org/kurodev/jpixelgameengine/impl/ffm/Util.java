package org.kurodev.jpixelgameengine.impl.ffm;

import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

public class Util {
    public static String cString(MemorySegment ptr, long maxLen) {
        if (ptr == MemorySegment.NULL) return null;
        return ptr.reinterpret(maxLen).getString(0, StandardCharsets.UTF_8);
    }
}
