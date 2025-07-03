package org.kurodev.jpixelgameengine.impl.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class EngineInitialiser {
    private static MemorySegment onUserCreateStub;
    private static MemorySegment onUserUpdateStub;
    private static MemorySegment onUserDestroyStub;
    private static MemorySegment onConsoleCommandStub;
    private static MemorySegment onTextEntryCompleteStub;

    static MemorySegment createOnUserCreateStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onUserCreate",
                        MethodType.methodType(boolean.class))
                .bindTo(binding);
        onUserCreateStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                arena
        );
        return onUserCreateStub;
    }

    static MemorySegment createOnUserUpdateStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onUserUpdate",
                        MethodType.methodType(boolean.class, float.class))
                .bindTo(binding);
        onUserUpdateStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_FLOAT),
                arena
        );
        return onUserUpdateStub;
    }

    static MemorySegment createOnUserDestroyStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onUserDestroy",
                        MethodType.methodType(boolean.class))
                .bindTo(binding);
        onUserDestroyStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                arena);
        return onUserDestroyStub;
    }

    static MemorySegment createOnConsoleCommandStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onConsoleCommand",
                        MethodType.methodType(boolean.class))
                .bindTo(binding);
        onConsoleCommandStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                arena
        );
        return onUserDestroyStub;
    }

    static MemorySegment createTextEntryCompleteStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onTextEntryComplete", MethodType.methodType(void.class))
                .bindTo(binding);
        onTextEntryCompleteStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                arena
        );
        return onUserDestroyStub;
    }
}
