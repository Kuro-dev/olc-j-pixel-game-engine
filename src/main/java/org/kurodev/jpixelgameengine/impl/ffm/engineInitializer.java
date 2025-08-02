package org.kurodev.jpixelgameengine.impl.ffm;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@SuppressWarnings("FieldCanBeLocal")
public class engineInitializer {
    //just store them so they don't get garbage collected
    private MemorySegment onUserCreateStub;
    private MemorySegment onUserUpdateStub;
    private MemorySegment onUserDestroyStub;
    private MemorySegment onConsoleCommandStub;
    private MemorySegment onTextEntryCompleteStub;

    MemorySegment createOnUserCreateStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onUserCreate",
                        MethodType.methodType(boolean.class))
                .bindTo(binding);
        return onUserCreateStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                arena
        );
    }

    MemorySegment createOnUserUpdateStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onUserUpdate",
                        MethodType.methodType(boolean.class, float.class))
                .bindTo(binding);
        return onUserUpdateStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_FLOAT),
                arena
        );
    }

    MemorySegment createOnUserDestroyStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onUserDestroy",
                        MethodType.methodType(boolean.class))
                .bindTo(binding);
        return onUserDestroyStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN),
                arena);
    }

    MemorySegment createOnConsoleCommandStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onConsoleCommand",
                        MethodType.methodType(boolean.class, MemorySegment.class))
                .bindTo(binding);
        return onConsoleCommandStub = linker.upcallStub(
                handle,
                FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS),
                arena
        );
    }

    MemorySegment createTextEntryCompleteStub(Linker linker, Arena arena, Object binding) throws NoSuchMethodException, IllegalAccessException {
        MethodHandle handle = MethodHandles.lookup()
                .findVirtual(binding.getClass(), "onTextEntryComplete", MethodType.methodType(void.class, MemorySegment.class))
                .bindTo(binding);
        return onTextEntryCompleteStub = linker.upcallStub(
                handle,
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
                arena
        );
    }
}
