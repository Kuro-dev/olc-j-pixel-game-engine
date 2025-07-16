package org.kurodev.jpixelgameengine.impl.ffm;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.Function;
import java.util.function.Supplier;

public class NativeEngineInstanceFunction<T> extends NativeFunction<T> {

    private final Supplier<MemorySegment> instanceSupplier;


    public NativeEngineInstanceFunction(String name, Supplier<MemorySegment> instanceSupplier, FunctionDescriptor descriptor) {
        super(name, descriptor);
        this.instanceSupplier = instanceSupplier;
    }

    public NativeEngineInstanceFunction(String symbolName, Supplier<MemorySegment> instanceSupplier, ValueLayout returnType, ValueLayout... args) {
        super(symbolName, returnType, args);
        this.instanceSupplier = instanceSupplier;
    }

    @Override
    public T invoke() {
        return super.invoke(instanceSupplier.get());
    }

    @Override
    public T invoke(Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = instanceSupplier.get();
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return super.invoke(newArgs);
    }

    @Override
    public T invokeObj(Function<MemorySegment, T> toObj) {
        return super.invokeObj(toObj, instanceSupplier.get());
    }

    @Override
    public T invokeObj(Function<MemorySegment, T> toObj, Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = instanceSupplier.get();
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return super.invokeObj(toObj, newArgs);
    }

    @Override
    public T invokeExact(Function<MemorySegment, T> toObj, Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = instanceSupplier.get();
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return super.invokeExact(toObj, newArgs);
    }
}
