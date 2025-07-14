package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.ValueLayout;

/**
 * Cache for fast access to frequently used methods to reduce the need for library lookups.
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

    private final NativeFunction<HWButton> getKey = createFn("getKey",
            FunctionDescriptor.of(HWButton.LAYOUT, ValueLayout.JAVA_INT));

    private final NativeFunction<HWButton> getMouseBtn = createFn("getMouse",
            FunctionDescriptor.of(HWButton.LAYOUT, ValueLayout.JAVA_INT));

    private final NativeFunction<Vector2D<Integer>> getMousePos = createFn("getMousePos",
            FunctionDescriptor.of(IntVector2D.LAYOUT));

    private final NativeFunction<Vector2D<Integer>> getWindowMousePos = createFn("getWindowMouse",
            FunctionDescriptor.of(IntVector2D.LAYOUT));

    private final NativeFunction<Integer> getMouseWheel = createFn("getMouseWheel", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> setScreenSize = createFn("setScreenSize",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

    private final NativeFunction<Void> drawString = createFn("drawString",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

    private final NativeFunction<Void> drawCircle = createFn("drawCircle",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

    private final NativeFunction<Void> fillCircle = createFn("fillCircle",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT));

    private final NativeFunction<Void> consoleClear = createFn("consoleClear", FunctionDescriptor.ofVoid());

    private final NativeFunction<Void> consoleShow = createFn("consoleShow", FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN));

    private final NativeFunction<Boolean> isConsoleShowing = createFn("isConsoleShowing", ValueLayout.JAVA_BOOLEAN);

    private final NativeFunction<Void> drawLine = createFn("drawLine",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT));

    private final NativeFunction<Void> drawRect = createFn("drawRect",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT));

    private final NativeFunction<Void> fillRect = createFn("fillRect",
            FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT,
                    ValueLayout.JAVA_INT));

    private final NativeFunction<Void> textEntryEnable = createFn("textEntryEnable", FunctionDescriptor.ofVoid(ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS));

    private final NativeFunction<String> textEntryGetString = createFn("textEntryGetString", ValueLayout.ADDRESS);

    private final NativeFunction<Integer> textEntryGetCursor = createFn("textEntryGetCursor", ValueLayout.JAVA_INT);

    private final NativeFunction<Boolean> isTextEntryEnabled = createFn("isTextEntryEnabled", ValueLayout.JAVA_BOOLEAN);


}
