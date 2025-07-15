package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;

/**
 * Cache for fast access to frequently used methods to reduce the need for library lookups.
 */
@Getter
@Accessors(fluent = true)
public class OlcMethods {


    private <T> NativeFunction<T> createFn(String name, MemoryLayout returnVal, MemoryLayout... args) {
        return new NativeFunction<T>(name, FunctionDescriptor.of(returnVal, args));
    }

    private <T> NativeFunction<T> createVoidFn(String name, MemoryLayout... args) {
        return new NativeFunction<T>(name, FunctionDescriptor.ofVoid(args));
    }

    /**
     * (int x, int y, int rgba)
     */
    private final NativeFunction<Boolean> draw = createFn("draw",
            ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    private final NativeFunction<Boolean> isFocused = createFn("isFocused", ValueLayout.JAVA_BOOLEAN);

    private final NativeFunction<HWButton> getKey = createFn("getKey", HWButton.LAYOUT, ValueLayout.JAVA_INT);

    private final NativeFunction<HWButton> getMouseBtn = createFn("getMouse", HWButton.LAYOUT, ValueLayout.JAVA_INT);

    private final NativeFunction<Vector2D<Integer>> getMousePos = createFn("getMousePos", IntVector2D.LAYOUT);

    private final NativeFunction<Vector2D<Integer>> getWindowMousePos = createFn("getWindowMouse", IntVector2D.LAYOUT);

    private final NativeFunction<Integer> getMouseWheel = createFn("getMouseWheel", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> setScreenSize = createVoidFn("setScreenSize", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    private final NativeFunction<Void> drawString = createVoidFn("drawString",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    private final NativeFunction<Void> drawCircle = createVoidFn("drawCircle",
            ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    private final NativeFunction<Void> fillCircle = createVoidFn("fillCircle",
            ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    private final NativeFunction<Void> consoleClear = createVoidFn("consoleClear");

    private final NativeFunction<Void> consoleShow = createVoidFn("consoleShow", ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN);

    private final NativeFunction<Boolean> isConsoleShowing = createFn("isConsoleShowing", ValueLayout.JAVA_BOOLEAN);

    private final NativeFunction<Void> drawLine = createVoidFn("drawLine",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    private final NativeFunction<Void> drawRect = createVoidFn("drawRect",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    private final NativeFunction<Void> fillRect = createVoidFn("fillRect",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    private final NativeFunction<Void> textEntryEnable = createVoidFn("textEntryEnable", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS);

    private final NativeFunction<String> textEntryGetString = createFn("textEntryGetString", ValueLayout.ADDRESS);

    private final NativeFunction<Integer> textEntryGetCursor = createFn("textEntryGetCursor", ValueLayout.JAVA_INT);

    private final NativeFunction<Boolean> isTextEntryEnabled = createFn("isTextEntryEnabled", ValueLayout.JAVA_BOOLEAN);

    private final NativeFunction<Void> drawSprite = createVoidFn("drawSprite",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_BYTE
    );

    private final NativeFunction<Void> drawPartialSprite = createVoidFn("drawPartialSprite",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_BYTE
    );

    private final NativeFunction<Vector2D<Integer>> getScreenPixelSize = createFn("getScreenPixelSize", IntVector2D.LAYOUT);

    private final NativeFunction<Vector2D<Integer>> getScreenSize = createFn("getScreenSize", IntVector2D.LAYOUT);

    private final NativeFunction<Void> setPixelMode = createVoidFn("setPixelMode", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> setDecalMode = createVoidFn("setDecalMode", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> setDecalStructure = createVoidFn("setDecalStructure", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> drawDecal = createVoidFn("drawDecal",
            FloatVector2D.LAYOUT,
            ValueLayout.ADDRESS,
            FloatVector2D.LAYOUT,
            Pixel.LAYOUT
    );

}
