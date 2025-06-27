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

    public MethodHandle getCachedHandle() {
        ensureInitialized();
        return cachedHandle;
    }

    @SneakyThrows
    public T invoke() {
        ensureInitialized();
        return (T) cachedHandle.invoke();
    }

    @SneakyThrows
    public T invokeObj(Function<MemorySegment, T> toObj) {
        if (arena == null) {
            arena = Arena.ofConfined();
        }
        ensureInitialized();
        var seg = (MemorySegment) cachedHandle.invoke(arena);
        return toObj.apply(seg);
    }


    @SneakyThrows
    public T invokeObj(Function<MemorySegment, T> toObj, Object... args) {
        if (arena == null) {
            arena = Arena.ofConfined();
        }
        ensureInitialized();

        // Create exact parameter types list
        Class<?>[] ptypes = new Class<?>[args.length + 1];
        ptypes[0] = SegmentAllocator.class;
        Arrays.fill(ptypes, 1, ptypes.length, Object.class);

        // Adapt the method handle
        MethodHandle adapted = cachedHandle.asType(
                MethodType.methodType(MemorySegment.class, ptypes)
        );

        // Build arguments array
        Object[] invokeArgs = new Object[args.length + 1];
        invokeArgs[0] = arena;
        System.arraycopy(args, 0, invokeArgs, 1, args.length);

        MemorySegment seg = (MemorySegment) adapted.invokeWithArguments(invokeArgs);
        return toObj.apply(seg);
    }

    @SneakyThrows
    public T invoke(Object... args) {
        ensureInitialized();
        return (T) cachedHandle.invokeWithArguments(args);
    }

}