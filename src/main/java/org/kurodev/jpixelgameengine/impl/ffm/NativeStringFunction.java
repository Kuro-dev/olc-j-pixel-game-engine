package org.kurodev.jpixelgameengine.impl.ffm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.function.Function;

public class NativeStringFunction extends NativeFunction<String> {
    private static final Logger log = LoggerFactory.getLogger(NativeStringFunction.class);
    private static int BUFFER_SIZE = 1024;

    public NativeStringFunction(String name, FunctionDescriptor descriptor) {
        super(name, descriptor);
    }

    public NativeStringFunction(String symbolName, ValueLayout returnType, ValueLayout... args) {
        super(symbolName, returnType, args);
    }

    public static void setBufferSize(int bufferSize) {
        BUFFER_SIZE = bufferSize;
    }

    private MemorySegment createBuffer() {
        if (cachedHandle == null)
            ensureInitialized();
        return arena.allocate(BUFFER_SIZE);
    }

    @Override
    public String invoke() {
        try {
            MemorySegment buffer = createBuffer();
            cachedHandle.invoke(buffer, BUFFER_SIZE);
            return Util.cString(buffer);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String invoke(Object... args) {
        try {
            MemorySegment buffer = createBuffer();
            Object[] argsActual = new Object[args.length + 2];
            argsActual[0] = buffer;
            argsActual[1] = BUFFER_SIZE;
            System.arraycopy(args, 0, argsActual, 2, args.length);
            cachedHandle.invokeWithArguments(argsActual);
            return Util.cString(buffer);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String invokeObj(Function<MemorySegment, String> toObj) {
        throw new UnsupportedOperationException("Not Supported, use Invoke");
    }

    @Override
    public String invokeObj(Function<MemorySegment, String> toObj, Object... args) {
        throw new UnsupportedOperationException("Not Supported, use Invoke");

    }

    @Override
    public String invokeExact(Function<MemorySegment, String> toObj, Object... args) {
        throw new UnsupportedOperationException("Not Supported, use Invoke");
    }

    @Override
    protected void ensureInitialized() {
        super.ensureInitialized();
        log.info("String buffer size is {}. You can adjust it's size using NativeStringFunction#setBufferSize(int)", BUFFER_SIZE);
    }
}
