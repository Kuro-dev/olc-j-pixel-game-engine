package org.kurodev.jpixelgameengine.impl.ffm;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.decal.DecalPatch;
import org.kurodev.jpixelgameengine.gfx.sprite.SpritePatch;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Cache for fast access to frequently used methods to reduce the need for library lookups.
 * All the methods described here need an Engine Instance to function.
 */
public class OlcMethods {

    final NativeFunction<Integer> construct = createFn("engine_construct", ValueLayout.JAVA_INT, //r-code
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
    final NativeFunction<Boolean> draw = createFn("draw", ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Boolean> isFocused = createFn("isFocused", ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<HWButton> getKey = createFn("getKey", HWButton.LAYOUT, ValueLayout.JAVA_INT);
    final NativeFunction<HWButton> getMouseBtn = createFn("getMouse", HWButton.LAYOUT, ValueLayout.JAVA_INT);
    final NativeFunction<Vector2D<Integer>> getMousePos = createFn("getMousePos", IntVector2D.LAYOUT);
    final NativeFunction<Vector2D<Integer>> getWindowMousePos = createFn("getWindowMouse", IntVector2D.LAYOUT);
    final NativeFunction<Integer> getMouseWheel = createFn("getMouseWheel", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getMouseX = createFn("getMouseX", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getMouseY = createFn("getMouseY", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getKeyMapCount = createFn("getKeyMapCount", ValueLayout.JAVA_INT);
    final NativeFunction<Long> getKeyMapNativeKeyAt = createFn("getKeyMapNativeKeyAt", ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getKeyMapEngineKeyAt = createFn("getKeyMapEngineKeyAt", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> setScreenSize = createVoidFn("setScreenSize", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Integer> showWindowFrame = createFn("showWindowFrame", ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Integer> screenWidth = createFn("screenWidth", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> screenHeight = createFn("screenHeight", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getDrawTargetWidth = createFn("getDrawTargetWidth", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getDrawTargetHeight = createFn("getDrawTargetHeight", ValueLayout.JAVA_INT);
    final NativeFunction<MemorySegment> getDrawTarget = createFn("getDrawTarget", ValueLayout.ADDRESS);
    final NativeFunction<Void> setDrawTargetSprite = createVoidFn("setDrawTargetSprite", ValueLayout.ADDRESS);
    final NativeFunction<Void> setDrawTargetLayer = createVoidFn("setDrawTargetLayer", ValueLayout.JAVA_BYTE, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Float> getElapsedTime = createFn("getElapsedTime", ValueLayout.JAVA_FLOAT);
    final NativeFunction<Vector2D<Integer>> getPixelSize = createFn("getPixelSize", IntVector2D.LAYOUT);
    final NativeFunction<Integer> getDroppedFilesCount = createFn("getDroppedFilesCount", ValueLayout.JAVA_INT);
    final NativeFunction<String> getDroppedFile = createStringFn("getDroppedFile", ValueLayout.JAVA_INT);
    final NativeFunction<Vector2D<Integer>> getDroppedFilesPoint = createFn("getDroppedFilesPoint", IntVector2D.LAYOUT);
    final NativeFunction<Void> drawString = createVoidFn("drawString", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> drawStringProp = createVoidFn("drawStringProp", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Vector2D<Integer>> getTextSize = createFn("getTextSize", IntVector2D.LAYOUT, ValueLayout.ADDRESS);
    final NativeFunction<Vector2D<Integer>> getTextSizeProp = createFn("getTextSizeProp", IntVector2D.LAYOUT, ValueLayout.ADDRESS);
    final NativeFunction<Void> drawCircle = createVoidFn("drawCircle", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> fillCircle = createVoidFn("fillCircle", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> consoleClear = createVoidFn("consoleClear");
    final NativeFunction<Void> consoleCaptureStdOut = createVoidFn("consoleCaptureStdOut", ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> consoleShow = createVoidFn("consoleShow", ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Boolean> isConsoleShowing = createFn("isConsoleShowing", ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> drawLine = createVoidFn("drawLine", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> drawRect = createVoidFn("drawRect", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> fillRect = createVoidFn("fillRect", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> drawTriangle = createVoidFn("drawTriangle", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> fillTriangle = createVoidFn("fillTriangle", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Void> fillTexturedTriangle = createVoidFn("fillTexturedTriangle", ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    final NativeFunction<Void> fillTexturedPolygon = createVoidFn("fillTexturedPolygon", ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
    final NativeFunction<Void> textEntryEnable = createVoidFn("textEntryEnable", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS);
    final NativeFunction<String> textEntryGetString = createStringFn("textEntryGetString");
    final NativeFunction<Integer> textEntryGetCursor = createFn("textEntryGetCursor", ValueLayout.JAVA_INT);
    final NativeFunction<Boolean> isTextEntryEnabled = createFn("isTextEntryEnabled", ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> drawSprite = createVoidFn("drawSprite", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE);
    final NativeFunction<Void> drawPartialSprite = createVoidFn("drawPartialSprite", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_BYTE);
    final NativeFunction<Vector2D<Integer>> getScreenPixelSize = createFn("getScreenPixelSize", IntVector2D.LAYOUT);
    final NativeFunction<Vector2D<Integer>> getScreenSize = createFn("getScreenSize", IntVector2D.LAYOUT);
    final NativeFunction<Void> setPixelMode = createVoidFn("setPixelMode", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getPixelMode = createFn("getPixelMode", ValueLayout.JAVA_INT);
    final NativeFunction<Void> setPixelModeCustom = createVoidFn("setPixelModeCustom", ValueLayout.ADDRESS);
    final NativeFunction<Void> setPixelBlend = createVoidFn("setPixelBlend", ValueLayout.JAVA_FLOAT);
    final NativeFunction<Void> printToConsole = createVoidFn("printToConsole", ValueLayout.ADDRESS);
    final NativeFunction<Void> flushDrawQueue = createVoidFn("flushDrawQueue", ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
    final NativeFunction<Void> setDecalMode = createVoidFn("setDecalMode", ValueLayout.JAVA_INT);
    final NativeFunction<Void> setDecalStructure = createVoidFn("setDecalStructure", ValueLayout.JAVA_INT);
    final NativeFunction<Void> drawDecal = createVoidFn("drawDecal", FloatVector2D.LAYOUT, ValueLayout.ADDRESS, FloatVector2D.LAYOUT, Pixel.LAYOUT);
    final NativeFunction<Void> drawPartialDecal = createVoidFn("drawPartialDecal", FloatVector2D.LAYOUT, ValueLayout.ADDRESS, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, Pixel.LAYOUT);
    final NativeFunction<Void> drawPartialDecalSized = createVoidFn("drawPartialDecalSized", FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, ValueLayout.ADDRESS, FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, Pixel.LAYOUT);
    // Additional fields based on the provided C++ function signatures
    final NativeFunction<Void> drawExplicitDecal = createVoidFn("DrawExplicitDecal", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d *pos (array)
            ValueLayout.ADDRESS, // const olc::vf2d *uv (array)
            ValueLayout.ADDRESS, // const olc::Pixel *col (array)
            ValueLayout.JAVA_INT // uint32_t elements
    );
    final NativeFunction<Void> drawWarpedDecal = createVoidFn("DrawWarpedDecal", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d (&pos)[4] or const olc::vf2d *pos or const std::array<olc::vf2d, 4> &pos
            Pixel.LAYOUT // const olc::Pixel &tint
    );
    final NativeFunction<Void> drawPartialWarpedDecal = createVoidFn("DrawPartialWarpedDecal", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const olc::vf2d (&pos)[4] or const olc::vf2d *pos or const std::array<olc::vf2d, 4> &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_size
            Pixel.LAYOUT // const olc::Pixel &tint
    );
    final NativeFunction<Void> drawRotatedDecal = createVoidFn("DrawRotatedDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            FloatVector2D.LAYOUT, // const olc::vf2d &scale
            Pixel.LAYOUT // const olc::Pixel &tint
    );
    final NativeFunction<Void> drawPartialRotatedDecal = createVoidFn("DrawPartialRotatedDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            FloatVector2D.LAYOUT, // const olc::vf2d &source_pos
            FloatVector2D.LAYOUT, // const olc::vf2d &source_size
            FloatVector2D.LAYOUT, // const olc::vf2d &scale
            Pixel.LAYOUT // const olc::Pixel &tint
    );
    final NativeFunction<Void> drawStringDecal = createVoidFn("DrawStringDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            Pixel.LAYOUT, // const Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );
    final NativeFunction<Void> drawStringPropDecal = createVoidFn("DrawStringPropDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            Pixel.LAYOUT, // const Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );
    final NativeFunction<Void> drawRectDecal = createVoidFn("DrawRectDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT // const olc::Pixel col
    );
    final NativeFunction<Void> fillRectDecal = createVoidFn("FillRectDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT // const olc::Pixel col
    );
    final NativeFunction<Void> gradientFillRectDecal = createVoidFn("GradientFillRectDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            FloatVector2D.LAYOUT, // const olc::vf2d &size
            Pixel.LAYOUT, // const olc::Pixel colTL
            Pixel.LAYOUT, // const olc::Pixel colBL
            Pixel.LAYOUT, // const olc::Pixel colBR
            Pixel.LAYOUT // const olc::Pixel colTR
    );
    final NativeFunction<Void> fillTriangleDecal = createVoidFn("FillTriangleDecal", FloatVector2D.LAYOUT, // const olc::vf2d &p0
            FloatVector2D.LAYOUT, // const olc::vf2d &p1
            FloatVector2D.LAYOUT, // const olc::vf2d &p2
            Pixel.LAYOUT // const olc::Pixel col
    );
    final NativeFunction<Void> gradientTriangleDecal = createVoidFn("GradientTriangleDecal", FloatVector2D.LAYOUT, // const olc::vf2d &p0
            FloatVector2D.LAYOUT, // const olc::vf2d &p1
            FloatVector2D.LAYOUT, // const olc::vf2d &p2
            Pixel.LAYOUT, // const olc::Pixel c0
            Pixel.LAYOUT, // const olc::Pixel c1
            Pixel.LAYOUT // const olc::Pixel c2
    );
    final NativeFunction<Void> drawPolygonDecal = createVoidFn("DrawPolygonDecal", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos (array/pointer)
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv (array/pointer)
            ValueLayout.JAVA_INT,
            Pixel.LAYOUT // const olc::Pixel tint (for the single tint case)
    );
    final NativeFunction<Void> drawPolygonDecalWithDepth = createVoidFn("DrawPolygonDecalWithDepth", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<float> &depth
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.JAVA_INT,
            Pixel.LAYOUT // const olc::Pixel tint
    );
    final NativeFunction<Void> drawPolygonDecalWithColors = createVoidFn("DrawPolygonDecalWithColors", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &tint (array/pointer)
            ValueLayout.JAVA_INT
    );
    final NativeFunction<Void> drawPolygonDecalWithColorsAndTint = createVoidFn("DrawPolygonDecalWithColorsAndTint", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &colours
            ValueLayout.JAVA_INT,
            Pixel.LAYOUT // const olc::Pixel tint
    );
    final NativeFunction<Void> drawPolygonDecalWithDepthAndColorsAndTint = createVoidFn("DrawPolygonDecalWithDepthAndColorsAndTint", ValueLayout.ADDRESS, // olc::Decal *decal
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &pos
            ValueLayout.ADDRESS, // const std::vector<float> &depth
            ValueLayout.ADDRESS, // const std::vector<olc::vf2d> &uv
            ValueLayout.ADDRESS, // const std::vector<olc::Pixel> &colours
            ValueLayout.JAVA_INT,
            Pixel.LAYOUT // const olc::Pixel tint
    );
    final NativeFunction<Void> drawLineDecal = createVoidFn("DrawLineDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos1
            FloatVector2D.LAYOUT, // const olc::vf2d &pos2
            Pixel.LAYOUT // Pixel p
    );
    final NativeFunction<Void> drawRotatedStringDecal = createVoidFn("DrawRotatedStringDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            Pixel.LAYOUT, // const olc::Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );
    final NativeFunction<Void> drawRotatedStringPropDecal = createVoidFn("DrawRotatedStringPropDecal", FloatVector2D.LAYOUT, // const olc::vf2d &pos
            ValueLayout.ADDRESS, // const std::string &sText (char pointer)
            ValueLayout.JAVA_FLOAT, // const float fAngle
            FloatVector2D.LAYOUT, // const olc::vf2d &center
            Pixel.LAYOUT, // const olc::Pixel col
            FloatVector2D.LAYOUT // const olc::vf2d &scale
    );
    final NativeFunction<Void> clear = createVoidFn("Clear", Pixel.LAYOUT // Pixel p
    );
    final NativeFunction<Void> clearBuffer = createVoidFn("ClearBuffer", Pixel.LAYOUT, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<MemorySegment> getFontSprite = createFn("GetFontSprite", ValueLayout.ADDRESS);
    final NativeFunction<Boolean> clipLineToDrawTarget = createFn("ClipLineToDrawTarget", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    final NativeFunction<Void> drawSpritePatch = createVoidFn("DrawSpritePatch", FloatVector2D.LAYOUT, SpritePatch.LAYOUT, FloatVector2D.LAYOUT);
    final NativeFunction<Void> drawDecalPatch = createVoidFn("DrawDecalPatch", FloatVector2D.LAYOUT, DecalPatch.LAYOUT, FloatVector2D.LAYOUT);
    final NativeFunction<Void> enablePixelTransfer = createVoidFn("EnablePixelTransfer", ValueLayout.JAVA_BOOLEAN);

    final NativeFunction<Void> resize = createVoidFn("resize", IntVector2D.LAYOUT, IntVector2D.LAYOUT);
    final NativeFunction<Vector2D<Integer>> getWindowSize = createFn("getWindowSize", IntVector2D.LAYOUT);
    final NativeFunction<Vector2D<Integer>> getWindowPos = createFn("getWindowPos", IntVector2D.LAYOUT);
    final NativeFunction<Integer> getFramerate = createFn("getFps", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getKeyPressCacheCount = createFn("getKeyPressCacheCount", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getKeyPressCacheAt = createFn("getKeyPressCacheAt", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<Integer> convertKeycode = createFn("convertKeycode", ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
    final NativeFunction<String> getKeySymbol = createStringFn("getKeySymbol", ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> setWindowTitle = createVoidFn("setWindowTitle", ValueLayout.ADDRESS);
    final NativeFunction<Void> gradientLineDecal = createVoidFn("GradientLineDecal", FloatVector2D.LAYOUT, FloatVector2D.LAYOUT, Pixel.LAYOUT, Pixel.LAYOUT, ValueLayout.JAVA_INT);
    final NativeFunction<Integer> createLayer = createFn("createLayer", ValueLayout.JAVA_INT);
    final NativeFunction<Integer> getLayerCount = createFn("getLayerCount", ValueLayout.JAVA_INT);
    final NativeFunction<Void> enableLayer = createVoidFn("enableLayer", ValueLayout.JAVA_BYTE, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Boolean> isLayerEnabled = createFn("isLayerEnabled", ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BYTE);
    final NativeFunction<Vector2D<Float>> getLayerOffset = createFn("getLayerOffset", FloatVector2D.LAYOUT, ValueLayout.JAVA_BYTE);
    final NativeFunction<Vector2D<Float>> getLayerScale = createFn("getLayerScale", FloatVector2D.LAYOUT, ValueLayout.JAVA_BYTE);
    final NativeFunction<Pixel> getLayerTint = createFn("getLayerTint", Pixel.LAYOUT, ValueLayout.JAVA_BYTE);
    final NativeFunction<Void> setLayerOffset = createVoidFn("SetLayerOffset", ValueLayout.JAVA_BYTE, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT);
    final NativeFunction<Void> setLayerScale = createVoidFn("SetLayerScale", ValueLayout.JAVA_BYTE, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT);
    final NativeFunction<Void> setLayerTint = createVoidFn("SetLayerTint", ValueLayout.JAVA_BYTE, Pixel.LAYOUT);
    final NativeFunction<Void> setLayerCustomRenderFunction = createVoidFn("SetLayerCustomRenderFunction", ValueLayout.JAVA_BYTE, ValueLayout.ADDRESS);
    final NativeFunction<Void> advManualRenderEnable = createVoidFn("adv_ManualRenderEnable", ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> advHardwareClip = createVoidFn("adv_HardwareClip", ValueLayout.JAVA_BOOLEAN, IntVector2D.LAYOUT, IntVector2D.LAYOUT, ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> advFlushLayer = createVoidFn("adv_FlushLayer", ValueLayout.JAVA_INT);
    final NativeFunction<Void> advFlushLayerDecals = createVoidFn("adv_FlushLayerDecals", ValueLayout.JAVA_INT);
    final NativeFunction<Void> advFlushLayerGpuTasks = createVoidFn("adv_FlushLayerGPUTasks", ValueLayout.JAVA_INT);
    final NativeFunction<Void> hw3dProjection = createVoidFn("HW3D_Projection", ValueLayout.ADDRESS);
    final NativeFunction<Void> hw3dEnableDepthTest = createVoidFn("HW3D_EnableDepthTest", ValueLayout.JAVA_BOOLEAN);
    final NativeFunction<Void> hw3dSetCullMode = createVoidFn("HW3D_SetCullMode", ValueLayout.JAVA_INT);
    final NativeFunction<Void> hw3dDrawObject = createVoidFn("HW3D_DrawObject", ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, Pixel.LAYOUT);
    final NativeFunction<Void> hw3dDrawLine = createVoidFn("HW3D_DrawLine", ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, Pixel.LAYOUT);
    final NativeFunction<Void> hw3dDrawLineBox = createVoidFn("HW3D_DrawLineBox", ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, Pixel.LAYOUT);

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

    private NativeFunction<String> createStringFn(String name, MemoryLayout... args) {
        MemoryLayout[] argsActual = new MemoryLayout[args.length + 3];
        argsActual[0] = ValueLayout.ADDRESS; // Character buffer
        argsActual[1] = ValueLayout.JAVA_INT;// buffer size
        argsActual[2] = ValueLayout.ADDRESS; // Engine Pointer
        System.arraycopy(args, 0, argsActual, 3, args.length);
        return new NativeStringFunction(name, FunctionDescriptor.of(ValueLayout.JAVA_INT, argsActual));
    }
}
