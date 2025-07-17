package org.kurodev.jpixelgameengine.impl.ffm;

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
 * All the methods described here need an Engine Instance to function.
 */
public class OlcMethods {

    private <T> NativeFunction<T> createFn(String name, MemoryLayout returnVal, MemoryLayout... args) {
        MemoryLayout[] argsActual = new MemoryLayout[args.length + 1];
        argsActual[0] = ValueLayout.ADDRESS;
        System.arraycopy(args, 0, argsActual, 1, args.length);

        return new NativeFunction<T>(name, FunctionDescriptor.of(returnVal, argsActual));
    }

    private <T> NativeFunction<T> createVoidFn(String name, MemoryLayout... args) {
        MemoryLayout[] argsActual = new MemoryLayout[args.length + 1];
        argsActual[0] = ValueLayout.ADDRESS;
        System.arraycopy(args, 0, argsActual, 1, args.length);
        return new NativeFunction<T>(name, FunctionDescriptor.ofVoid(argsActual));
    }

    final NativeFunction<Integer> construct = createFn("engine_construct",
            ValueLayout.JAVA_INT, //r-code
            ValueLayout.JAVA_INT, // width
            ValueLayout.JAVA_INT, // height
            ValueLayout.JAVA_INT, // pixel_w
            ValueLayout.JAVA_INT, // pixel_h
            ValueLayout.JAVA_BOOLEAN, // full_screen,
            ValueLayout.JAVA_BOOLEAN, //  bool vsync,
            ValueLayout.JAVA_BOOLEAN, // bool cohesion,
            ValueLayout.JAVA_BOOLEAN  // bool realwindow
    );

    /**
     * (int x, int y, int rgba)
     */
    final NativeFunction<Boolean> draw = createFn("draw",
            ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    final NativeFunction<Boolean> isFocused = createFn("isFocused", ValueLayout.JAVA_BOOLEAN);

    final NativeFunction<HWButton> getKey = createFn("getKey", HWButton.LAYOUT, ValueLayout.JAVA_INT);

    final NativeFunction<HWButton> getMouseBtn = createFn("getMouse", HWButton.LAYOUT, ValueLayout.JAVA_INT);

    final NativeFunction<Vector2D<Integer>> getMousePos = createFn("getMousePos", IntVector2D.LAYOUT);

    final NativeFunction<Vector2D<Integer>> getWindowMousePos = createFn("getWindowMouse", IntVector2D.LAYOUT);

    final NativeFunction<Integer> getMouseWheel = createFn("getMouseWheel", ValueLayout.JAVA_INT);

    final NativeFunction<Void> setScreenSize = createVoidFn("setScreenSize", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    final NativeFunction<Void> drawString = createVoidFn("drawString",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    final NativeFunction<Void> drawCircle = createVoidFn("drawCircle",
            ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    final NativeFunction<Void> fillCircle = createVoidFn("fillCircle",
            ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);

    final NativeFunction<Void> consoleClear = createVoidFn("consoleClear");

    final NativeFunction<Void> consoleShow = createVoidFn("consoleShow", ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN);

    final NativeFunction<Boolean> isConsoleShowing = createFn("isConsoleShowing", ValueLayout.JAVA_BOOLEAN);

    final NativeFunction<Void> drawLine = createVoidFn("drawLine",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    final NativeFunction<Void> drawRect = createVoidFn("drawRect",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    final NativeFunction<Void> fillRect = createVoidFn("fillRect",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);

    final NativeFunction<Void> textEntryEnable = createVoidFn("textEntryEnable", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS);

    final NativeFunction<String> textEntryGetString = createFn("textEntryGetString", ValueLayout.ADDRESS);

    final NativeFunction<Integer> textEntryGetCursor = createFn("textEntryGetCursor", ValueLayout.JAVA_INT);

    final NativeFunction<Boolean> isTextEntryEnabled = createFn("isTextEntryEnabled", ValueLayout.JAVA_BOOLEAN);

    final NativeFunction<Void> drawSprite = createVoidFn("drawSprite",
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_BYTE
    );

    final NativeFunction<Void> drawPartialSprite = createVoidFn("drawPartialSprite",
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

    final NativeFunction<Vector2D<Integer>> getScreenPixelSize = createFn("getScreenPixelSize", IntVector2D.LAYOUT);

    final NativeFunction<Vector2D<Integer>> getScreenSize = createFn("getScreenSize", IntVector2D.LAYOUT);

    final NativeFunction<Void> setPixelMode = createVoidFn("setPixelMode", ValueLayout.JAVA_INT);

    final NativeFunction<Void> printToConsole = createVoidFn("printToConsole", ValueLayout.ADDRESS);

    final NativeFunction<Void> setDecalMode = createVoidFn("setDecalMode", ValueLayout.JAVA_INT);

    final NativeFunction<Void> setDecalStructure = createVoidFn("setDecalStructure", ValueLayout.JAVA_INT);

    final NativeFunction<Void> drawDecal = createVoidFn("drawDecal",
            FloatVector2D.LAYOUT,
            ValueLayout.ADDRESS,
            FloatVector2D.LAYOUT,
            Pixel.LAYOUT
    );

    final NativeFunction<Void> drawPartialDecal = createVoidFn("drawPartialDecal",
            FloatVector2D.LAYOUT,
            ValueLayout.ADDRESS,
            FloatVector2D.LAYOUT,
            FloatVector2D.LAYOUT,
            FloatVector2D.LAYOUT,
            Pixel.LAYOUT
    );

    // Additional fields based on the provided C++ function signatures
    final NativeFunction<Void> drawExplicitDecal = createVoidFn("DrawExplicitDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d *pos (array)
            ValueLayout.ADDRESS, // const olc::vf2d *uv (array)
            ValueLayout.ADDRESS, // const olc::Pixel *col (array)
            ValueLayout.JAVA_INT // uint32_t elements
    );

    final NativeFunction<Void> drawWarpedDecal = createVoidFn("DrawWarpedDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d (&pos)[4] or const olc::vf2d *pos or const std::array<olc::vf2d, 4> &pos
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    final NativeFunction<Void> drawPartialWarpedDecal = createVoidFn("DrawPartialWarpedDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d (&pos)[4] or const olc::vf2d *pos or const std::array<olc::vf2d, 4> &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_size
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    final NativeFunction<Void> drawRotatedDecal = createVoidFn("DrawRotatedDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            FloatVector2D.LAYOUT, // const olc::vf2d &scale
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    final NativeFunction<Void> drawPartialRotatedDecal = createVoidFn("DrawPartialRotatedDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            FloatVector2D.LAYOUT, // const olc::vf2d &source_pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_size
            FloatVector2D.LAYOUT, // const olc::vf2d &scale
            Pixel.LAYOUT // const olc::Pixel &tint
    );

    final NativeFunction<Void> drawStringDecal = createVoidFn("DrawStringDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            Pixel.LAYOUT, // const Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    final NativeFunction<Void> drawStringPropDecal = createVoidFn("DrawStringPropDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            Pixel.LAYOUT, // const Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    final NativeFunction<Void> drawRectDecal = createVoidFn("DrawRectDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT // const olc::Pixel col
    );

    final NativeFunction<Void> fillRectDecal = createVoidFn("FillRectDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT // const olc::Pixel col
    );

    final NativeFunction<Void> gradientFillRectDecal = createVoidFn("GradientFillRectDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT, // const olc::Pixel colTL
            Pixel.LAYOUT, // const olc::Pixel colBL
            Pixel.LAYOUT, // const olc::Pixel colBR
            Pixel.LAYOUT // const olc::Pixel colTR
    );

    final NativeFunction<Void> fillTriangleDecal = createVoidFn("FillTriangleDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &p0
            FloatVector2D.LAYOUT, // const olc::vf2d &p1
            FloatVector2D.LAYOUT, // const olc::vf2d &p2
            Pixel.LAYOUT // const olc::Pixel col
    );

    final NativeFunction<Void> gradientTriangleDecal = createVoidFn("GradientTriangleDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &p0
            FloatVector2D.LAYOUT, // const olc::vf2d &p1
            FloatVector2D.LAYOUT, // const olc::vf2d &p2
            Pixel.LAYOUT, // const olc::Pixel c0
            Pixel.LAYOUT, // const olc::Pixel c1
            Pixel.LAYOUT // const olc::Pixel c2
    );

    final NativeFunction<Void> drawPolygonDecal = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos (array/pointer)
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv (array/pointer)
            Pixel.LAYOUT // const olc::Pixel tint (for the single tint case)
    );

    final NativeFunction<Void> drawPolygonDecalWithDepth = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<float> &depth
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            Pixel.LAYOUT // const olc::Pixel tint
    );

    final NativeFunction<Void> drawPolygonDecalWithColors = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS // const std::vector<olc::Pixel> &tint (array/pointer)
    );

    final NativeFunction<Void> drawPolygonDecalWithColorsAndTint = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &colours
            Pixel.LAYOUT // const olc::Pixel tint
    );

    final NativeFunction<Void> drawPolygonDecalWithDepthAndColorsAndTint = createVoidFn("DrawPolygonDecal",
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<float> &depth
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &colours
            Pixel.LAYOUT // const olc::Pixel tint
    );


    final NativeFunction<Void> drawLineDecal = createVoidFn("DrawLineDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos1
            FloatVector2D.LAYOUT, // const olc::vf2d &pos2
            Pixel.LAYOUT // Pixel p
    );

    final NativeFunction<Void> drawRotatedStringDecal = createVoidFn("DrawRotatedStringDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            Pixel.LAYOUT, // const olc::Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    final NativeFunction<Void> drawRotatedStringPropDecal = createVoidFn("DrawRotatedStringPropDecal",
            FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            Pixel.LAYOUT, // const olc::Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );

    final NativeFunction<Void> clear = createVoidFn("Clear",
            Pixel.LAYOUT // Pixel p
    );
}