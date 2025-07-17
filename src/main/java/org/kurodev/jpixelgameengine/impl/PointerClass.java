package org.kurodev.jpixelgameengine.impl;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;

/**
 * Wrapper class to turn a java class into a struct instance.
 * Will reuse the same reference
 */
public abstract class PointerClass {
    @SuppressWarnings("FieldCanBeLocal")
    private Arena arena;
    private MemorySegment ptr = null;

    /**
     * Returns this instance as a pointer address for use in C code
     * Allocates a new block of Memory if necessary, and then reuses it.
     *
     * @return The pointer address.
     */
    public final MemorySegment toPtr() {
        if (ptr == null) {
            this.arena = Arena.ofAuto();
            ptr = arena.allocate(getLayout());
        }
        return toPtr(ptr);
    }


    protected abstract MemorySegment toPtr(MemorySegment ptr);

    /**
     * Invoked only once to fetch the underlying memory layout
     */
    protected abstract MemoryLayout getLayout();
}
