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

    private final NativeFunction<Void> printToConsole = createVoidFn("printToConsole", ValueLayout.ADDRESS);

    private final NativeFunction<Void> setDecalMode = createVoidFn("setDecalMode", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> setDecalStructure = createVoidFn("setDecalStructure", ValueLayout.JAVA_INT);

    private final NativeFunction<Void> drawDecal = createVoidFn("drawDecal",
            FloatVector2D.LAYOUT,
            ValueLayout.ADDRESS,
            FloatVector2D.LAYOUT,
            Pixel.LAYOUT
    );

    private final NativeFunction<Void> drawPartialDecal = createVoidFn("drawPartialDecal",
            FloatVector2D.LAYOUT,
            ValueLayout.ADDRESS,
            FloatVector2D.LAYOUT,
            FloatVector2D.LAYOUT,
            FloatVector2D.LAYOUT,
            Pixel.LAYOUT
    );

    // Additional fields based on the provided C++ function signatures
    private final NativeFunction<Void> drawExplicitDecal = createVoidFn("DrawExplicitDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d *pos (array)
            ValueLayout.ADDRESS, // const olc::vf2d *uv (array)
            ValueLayout.ADDRESS, // const olc::Pixel *col (array)
            ValueLayout.JAVA_INT // uint32_t elements
    );

    private final NativeFunction<Void> drawWarpedDecal = createVoidFn("DrawWarpedDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d (&pos)[4] or const olc::vf2d *pos or const std::array<olc::vf2d, 4> &pos
            Pixel.LAYOUT // const olc::Pixel &tint
    );
    // Note: Overloaded methods in C++ typically map to a single native function
    // in JNI/FFM if their signatures can be resolved at runtime or if distinct
    // C functions are exposed. Assuming a single entry point for simplicity here.

    private final NativeFunction<Void> drawPartialWarpedDecal = createVoidFn("DrawPartialWarpedDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d (&pos)[4] or const olc::vf2d *pos or const std::array<olc::vf2d, 4> &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_size
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    private final NativeFunction<Void> drawRotatedDecal = createVoidFn("DrawRotatedDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            FloatVector2D.LAYOUT, // const olc::vf2d &scale
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    private final NativeFunction<Void> drawPartialRotatedDecal = createVoidFn("DrawPartialRotatedDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            FloatVector2D.LAYOUT, // const olc::vf2d &source_pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_size
            FloatVector2D.LAYOUT, // const olc::vf2d &scale
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    private final NativeFunction<Void> drawStringDecal = createVoidFn("DrawStringDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            Pixel.LAYOUT, // const Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    private final NativeFunction<Void> drawStringPropDecal = createVoidFn("DrawStringPropDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            Pixel.LAYOUT, // const Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    private final NativeFunction<Void> drawRectDecal = createVoidFn("DrawRectDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT // const olc::Pixel col
    );

    private final NativeFunction<Void> fillRectDecal = createVoidFn("FillRectDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT // const olc::Pixel col
    );

    private final NativeFunction<Void> gradientFillRectDecal = createVoidFn("GradientFillRectDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT, // const olc::Pixel colTL
            Pixel.LAYOUT, // const olc::Pixel colBL
            Pixel.LAYOUT, // const olc::Pixel colBR
            Pixel.LAYOUT // const olc::Pixel colTR
    );

    private final NativeFunction<Void> fillTriangleDecal = createVoidFn("FillTriangleDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &p0
            FloatVector2D.LAYOUT, // const olc::vf2d &p1
            FloatVector2D.LAYOUT, // const olc::vf2d &p2
            Pixel.LAYOUT // const olc::Pixel col
    );

    private final NativeFunction<Void> gradientTriangleDecal = createVoidFn("GradientTriangleDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &p0
            FloatVector2D.LAYOUT, // const olc::vf2d &p1
            FloatVector2D.LAYOUT, // const olc::vf2d &p2
            Pixel.LAYOUT, // const olc::Pixel c0
            Pixel.LAYOUT, // const olc::Pixel c1
            Pixel.LAYOUT // const olc::Pixel c2
    );

    private final NativeFunction<Void> drawPolygonDecal = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos (array/pointer)
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv (array/pointer)
            Pixel.LAYOUT // const olc::Pixel tint (for the single tint case)
    );
    // Overloaded drawPolygonDecal:
    // To handle std::vector<float> depth and std::vector<olc::Pixel> colours,
    // you'd typically need separate native methods if the underlying C++ library
    // doesn't expose a single flexible entry point.
    // For simplicity, assuming a common signature, but in a real FFM scenario,
    // you'd likely have distinct native function calls for each C++ overload.
    // So, for now, only one is represented, but in practice, you might need:
    // drawPolygonDecalWithDepth, drawPolygonDecalWithColors, etc.
    // Or, if the C++ side has a single function that takes optional parameters,
    // your FFM binding would reflect that.
    // Given the multiple C++ overloads for `DrawPolygonDecal`,
    // here are a few representations. In a real FFM scenario,
    // these would likely map to distinct native functions if the C++ library
    // does not provide a single entry point for all permutations.

    private final NativeFunction<Void> drawPolygonDecalWithDepth = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<float> &depth
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            Pixel.LAYOUT // const olc::Pixel tint
    );

    private final NativeFunction<Void> drawPolygonDecalWithColors = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS // const std::vector<olc::Pixel> &tint (array/pointer)
    );

    private final NativeFunction<Void> drawPolygonDecalWithColorsAndTint = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &colours
            Pixel.LAYOUT // const olc::Pixel tint
    );

    private final NativeFunction<Void> drawPolygonDecalWithDepthAndColorsAndTint = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<float> &depth
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &colours
            Pixel.LAYOUT // const olc::Pixel tint
    );


    private final NativeFunction<Void> drawLineDecal = createVoidFn("DrawLineDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos1
            FloatVector2D.LAYOUT, // const olc::vf2d &pos2
            Pixel.LAYOUT // Pixel p
    );

    private final NativeFunction<Void> drawRotatedStringDecal = createVoidFn("DrawRotatedStringDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            Pixel.LAYOUT, // const olc::Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    private final NativeFunction<Void> drawRotatedStringPropDecal = createVoidFn("DrawRotatedStringPropDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            Pixel.LAYOUT, // const olc::Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    private final NativeFunction<Void> clear = createVoidFn("Clear",
            Pixel.LAYOUT // Pixel p
    );
}