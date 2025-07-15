package org.kurodev.jpixelgameengine.impl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

public class MemUtil {

    /**
     * Packs an array of PointerClass objects into one contiguous MemorySegment.
     *
     * @param arena the Arena that owns the memory (lifetime ≥ the native call)
     * @param array the Java objects that each know how to describe & serialise themselves
     * @return a segment whose base address can be passed as T* to native code
     */
    public static <T extends PointerClass> MemorySegment toArrayPtr(Arena arena, T[] array) {
        if (array == null || array.length == 0) {
            return MemorySegment.NULL;
        }
        MemoryLayout elemLayout = array[0].getLayout();      // size & alignment
        long elemSize = elemLayout.byteSize();
        long elemAlign = elemLayout.byteAlignment();
        MemoryLayout arrayLayout = MemoryLayout.sequenceLayout(array.length, elemLayout);
        MemorySegment seg = arena.allocate(arrayLayout, elemAlign);
        for (int i = 0; i < array.length; i++) {
            MemorySegment src = array[i].toPtr();
            MemorySegment dst = seg.asSlice(i * elemSize, elemSize);
            dst.copyFrom(src);
        }
        return seg;
    }


    /* ── BYTE ─────────────────────────────────────────────────────────────── */
    public static MemorySegment toArrayPtr(Arena arena, byte[] src) {
        if (src == null || src.length == 0) return MemorySegment.NULL;
        MemorySegment seg = arena.allocate(src.length, ValueLayout.JAVA_BYTE.byteAlignment());
        seg.copyFrom(MemorySegment.ofArray(src));
        return seg;
    }

    /* ── SHORT ────────────────────────────────────────────────────────────── */
    public static MemorySegment toArrayPtr(Arena arena, short[] src) {
        if (src == null || src.length == 0) return MemorySegment.NULL;
        long bytes = (long) src.length * ValueLayout.JAVA_SHORT.byteSize();
        MemorySegment seg = arena.allocate(bytes, ValueLayout.JAVA_SHORT.byteAlignment());
        VarHandle vh = ValueLayout.JAVA_SHORT.arrayElementVarHandle();
        for (int i = 0; i < src.length; i++) vh.set(seg, (long) i, src[i]);
        return seg;
    }

    /* ── INT ──────────────────────────────────────────────────────────────── */
    public static MemorySegment toArrayPtr(Arena arena, int[] src) {
        if (src == null || src.length == 0) return MemorySegment.NULL;
        long bytes = (long) src.length * ValueLayout.JAVA_INT.byteSize();
        MemorySegment seg = arena.allocate(bytes, ValueLayout.JAVA_INT.byteAlignment());
        VarHandle vh = ValueLayout.JAVA_INT.arrayElementVarHandle();
        for (int i = 0; i < src.length; i++) vh.set(seg, (long) i, src[i]);
        return seg;
    }

    /* ── LONG ─────────────────────────────────────────────────────────────── */
    public static MemorySegment toArrayPtr(Arena arena, long[] src) {
        if (src == null || src.length == 0) return MemorySegment.NULL;
        long bytes = (long) src.length * ValueLayout.JAVA_LONG.byteSize();
        MemorySegment seg = arena.allocate(bytes, ValueLayout.JAVA_LONG.byteAlignment());
        VarHandle vh = ValueLayout.JAVA_LONG.arrayElementVarHandle();
        for (int i = 0; i < src.length; i++) vh.set(seg, (long) i, src[i]);
        return seg;
    }

    /* ── FLOAT ────────────────────────────────────────────────────────────── */
    public static MemorySegment toArrayPtr(Arena arena, float[] src) {
        if (src == null || src.length == 0) return MemorySegment.NULL;
        long bytes = (long) src.length * ValueLayout.JAVA_FLOAT.byteSize();
        MemorySegment seg = arena.allocate(bytes, ValueLayout.JAVA_FLOAT.byteAlignment());
        VarHandle vh = ValueLayout.JAVA_FLOAT.arrayElementVarHandle();
        for (int i = 0; i < src.length; i++) vh.set(seg, (long) i, src[i]);
        return seg;
    }

    /* ── DOUBLE ───────────────────────────────────────────────────────────── */
    public static MemorySegment toArrayPtr(Arena arena, double[] src) {
        if (src == null || src.length == 0) return MemorySegment.NULL;
        long bytes = (long) src.length * ValueLayout.JAVA_DOUBLE.byteSize();
        MemorySegment seg = arena.allocate(bytes, ValueLayout.JAVA_DOUBLE.byteAlignment());
        VarHandle vh = ValueLayout.JAVA_DOUBLE.arrayElementVarHandle();
        for (int i = 0; i < src.length; i++) vh.set(seg, (long) i, src[i]);
        return seg;
    }
}
