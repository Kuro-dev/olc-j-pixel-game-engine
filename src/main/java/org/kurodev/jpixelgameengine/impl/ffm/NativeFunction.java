package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.SneakyThrows;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Wrapper for native functions called in library code.
 * The function will only initialise on first call, and after that cache for repeated calls.
 * This means that the overhead should be minimal if the function isn't needed.
 *
 * @param <T> Return type of this function
 */
@SuppressWarnings("unchecked")
public class NativeFunction<T> {
    private final String symbolName;
    private final FunctionDescriptor descriptor;
    private Arena arena = null;

    private volatile MethodHandle cachedHandle;

    public NativeFunction(String name, FunctionDescriptor descriptor) {
        this.symbolName = name;
        this.descriptor = descriptor;
    }

    public NativeFunction(String symbolName, ValueLayout returnType, ValueLayout... args) {
        this(symbolName, FunctionDescriptor.of(returnType, args));
    }

    private void ensureInitialized() {
        if (cachedHandle != null) {
            return;
        }
        synchronized (this) {
            if (cachedHandle == null) {
                try {
                    MemorySegment symbol = PixelGameEngine.LIB
                            .find(symbolName)
                            .orElseThrow(() -> new RuntimeException("Symbol not found: " + symbolName));

                    this.cachedHandle = PixelGameEngine.LINKER.downcallHandle(symbol, descriptor);
                } catch (Throwable e) {
                    throw new RuntimeException("Failed to initialize native function", e);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NativeFunction<?> that = (NativeFunction<?>) o;
        return Objects.equals(symbolName, that.symbolName) && Objects.equals(descriptor, that.descriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbolName, descriptor);
    }

    @Override
    public String toString() {
        return "NativeFn{'" + symbolName + "' Initialised: " + (cachedHandle != null) + '}';
    }

    /**
     * Invokes this method and returns the result, automatically casted to generic type.
     *
     * @return T
     * @implNote For any non-primitive types {@link #invokeObj(Function)} must be used.
     * @see #invokeObj(Function)
     * @see #invokeObj(Function, Object...)
     */
    @SneakyThrows
    public T invoke() {
        ensureInitialized();
        return (T) cachedHandle.invoke();
    }

    /**
     * Invokes this method and returns the result, automatically casted to generic type.
     *
     * @param args Method arguments
     * @return T
     * @implNote For any non-primitive types {@link #invokeObj(Function)} must be used.
     * @see #invokeObj(Function)
     * @see #invokeObj(Function, Object...)
     */
    @SneakyThrows
    public T invoke(Object... args) {
        ensureInitialized();
        return (T) cachedHandle.invokeWithArguments(args);
    }

    /**
     * Invokes this method and returns the result, converted to the return type.
     * Works for Objects, probably also for primitive types, but untested. Use {@link #invoke()} for that.
     *
     * @param toObj Mapper function to turn memory segment into the desired type. Usually a specialized constructor.
     * @return T
     */
    @SneakyThrows
    public T invokeObj(Function<MemorySegment, T> toObj) {
        if (arena == null) {
            arena = Arena.ofAuto();
        }
        ensureInitialized();
        var seg = (MemorySegment) cachedHandle.invoke(arena);
        return toObj.apply(seg);
    }

    /**
     * Invokes this method and returns the result, converted to the return type.
     * Works for Objects, probably also for primitive types, but untested. Use {@link #invoke()} for that.
     *
     * @param toObj Mapper function to turn memory segment into the desired type. Usually a specialized constructor.
     * @param args  Method arguments.
     * @return T
     */
    @SneakyThrows
    public T invokeObj(Function<MemorySegment, T> toObj, Object... args) {
        if (arena == null) {
            //Create arena only on first call, otherwise don't waste the memory
            arena = Arena.ofConfined();
        }
        ensureInitialized();

        // Create exact parameter types list
        Class<?>[] ptypes = new Class<?>[args.length + 1];
        ptypes[0] = SegmentAllocator.class;
        Arrays.fill(ptypes, 1, ptypes.length, Object.class);

        // Methodhandle needs to be adapted to include the MemorySegment and the types of the arguments.
        // otherwise will throw classCastException
        MethodHandle adapted = cachedHandle.asType(
                MethodType.methodType(MemorySegment.class, ptypes)
        );

        Object[] invokeArgs = new Object[args.length + 1];
        invokeArgs[0] = arena;
        System.arraycopy(args, 0, invokeArgs, 1, args.length);

        MemorySegment seg = (MemorySegment) adapted.invokeWithArguments(invokeArgs);
        return toObj.apply(seg);
    }


}