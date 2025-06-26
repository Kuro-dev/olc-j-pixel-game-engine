package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;

/**
 * Cache for fast access to frequently used methods to reduce the need for cache map lookups.
 */
@Getter
@Accessors(fluent = true)
public class OlcMethods {

    private <T> NativeFunction<T> createFn(String name, ValueLayout returnVal, ValueLayout... args) {
        return new NativeFunction<T>(name, returnVal, args);
    }

    private <T> NativeFunction<T> createFn(String name, FunctionDescriptor descriptor) {
        return new NativeFunction<T>(name, descriptor);
    }

    /**
     * (int x, int y, int rgba)
     */
    private final NativeFunction<Boolean> draw = createFn("draw",
            ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    private final NativeFunction<Boolean> isFocused = createFn("isFocused", ValueLayout.JAVA_BOOLEAN);

    private final NativeFunction<HWButton> getKey = createFn("getKey", FunctionDescriptor.of(HWButton.LAYOUT, ValueLayout.JAVA_INT));

    private final NativeFunction<HWButton> getMouseBtn = createFn("getMouse", FunctionDescriptor.of(HWButton.LAYOUT, ValueLayout.JAVA_INT));

    private final NativeFunction<Vector2D<Integer>> getMousePos = createFn("getMousePos",
            FunctionDescriptor.of(IntVector2D.LAYOUT));

    private final NativeFunction<Vector2D<Integer>> getWindowMousePos = createFn("getWindowMouse",
            FunctionDescriptor.of(IntVector2D.LAYOUT));

    private final NativeFunction<Integer> getMouseWheel = createFn("getMouseWheel", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> setScreenSize = createFn("setScreenSize",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));
}
