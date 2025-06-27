package org.kurodev.jpixelgameengine.impl.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class EngineInitialiser {
    private static MemorySegment onUserCreateStub;
    private static MemorySegment onUserUpdateStub;
    private static MemorySegment onUserDestroyStub;

     static MemorySegment createOnUserCreateStub(Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        if (onUserCreateStub == null) {
            MethodHandle onCreateHandle = MethodHandles.lookup()
                    .findVirtual(binding.getClass(), "onUserCreate",
                            MethodType.methodType(boolean.class))
                    .bindTo(binding);
            onUserCreateStub = PixelGameEngine.LINKER.upcallStub(
                    onCreateHandle,
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                    arena
            );
        }
        return onUserCreateStub;
    }

     static MemorySegment createOnUserUpdateStub(Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        if (onUserUpdateStub == null) {
            MethodHandle onUpdateHandle = MethodHandles.lookup()
                    .findVirtual(binding.getClass(), "onUserUpdate",
                            MethodType.methodType(boolean.class, float.class))
                    .bindTo(binding);
            onUserUpdateStub = PixelGameEngine.LINKER.upcallStub(
                    onUpdateHandle,
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_FLOAT),
                    arena
            );
        }
        return onUserUpdateStub;
    }

     static MemorySegment createOnUserDestroyStub(Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        if (onUserDestroyStub == null) {
            MethodHandle onCreateHandle = MethodHandles.lookup()
                    .findVirtual(binding.getClass(), "onUserDestroy",
                            MethodType.methodType(boolean.class))
                    .bindTo(binding);
            onUserDestroyStub = PixelGameEngine.LINKER.upcallStub(
                    onCreateHandle,
                    FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                    arena
            );
        }
        return onUserDestroyStub;
    }
}
