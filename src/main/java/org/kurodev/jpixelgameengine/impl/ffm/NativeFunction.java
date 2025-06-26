package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.SneakyThrows;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class NativeFunction<T> {
    private final String symbolName;
    private final FunctionDescriptor descriptor;


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
    public T invoke(Object... args) {
        ensureInitialized();
        return (T) cachedHandle.invokeWithArguments(args);
    }

}