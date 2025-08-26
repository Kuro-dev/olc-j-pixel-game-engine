package org.kurodev.jpixelgameengine.impl.ffm;

import org.kurodev.NativeLoader;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.decal.DecalMode;
import org.kurodev.jpixelgameengine.gfx.decal.DecalStructure;
import org.kurodev.jpixelgameengine.gfx.sprite.FlipMode;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.impl.MemUtil;
import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;
import org.kurodev.jpixelgameengine.impl.PixelgameEngineReturnCode;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.*;
import java.lang.ref.Cleaner;

/**
 * Due to how FFM works this class must be inherited by a Named (NOT ANONYMOUS) class. Otherwise, it will fail.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public abstract class PixelGameEngine implements Cleaner.Cleanable {
    static final Linker LINKER = Linker.nativeLinker();
    static final SymbolLookup LIB = SymbolLookup.loaderLookup();
    private static final Logger log = LoggerFactory.getLogger(PixelGameEngine.class);
    private static final NativeFunction<Void> GAME_ENGINE_DESTROY = new NativeFunction<>("gameEngine_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<MemorySegment> NATIVE_CONSTRUCTOR = new NativeFunction<>("createGameEngineInstance",
            FunctionDescriptor.of(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS)
    );

    static {
        try {
            NativeLoader.loadLibraries();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Arena arena;
    private final MemorySegment instancePtr;
    private final OlcMethods methods;
    private final engineInitializer engineInitializer = new engineInitializer();

    public PixelGameEngine(int width, int height, int pixelWidth, int pixelHeight) {
        this(width, height, pixelWidth, pixelHeight, false, false, false, false);
    }

    //int32_t screen_w, int32_t screen_h, int32_t pixel_w, int32_t pixel_h, bool full_screen, bool vsync, bool cohesion, bool realwindow
    //bool full_screen = false, bool vsync = false, bool cohesion = false, bool realwindow = false
    public PixelGameEngine(int width, int height, int pixelWidth, int pixelHeight, boolean fullScreen, boolean vSync, boolean cohesion, boolean realWindow) {
        this.arena = Arena.ofAuto();
        try {
            MemorySegment onUserCreateStub = engineInitializer.createOnUserCreateStub(LINKER, arena, this);
            var onUserUpdateStub = engineInitializer.createOnUserUpdateStub(LINKER, arena, this);
            var onUserDestroyStub = engineInitializer.createOnUserDestroyStub(LINKER, arena, this);
            var onConsoleCommandStub = engineInitializer.createOnConsoleCommandStub(LINKER, arena, this);
            var onTextEntryCompleteStub = engineInitializer.createTextEntryCompleteStub(LINKER, arena, this);
            instancePtr = NATIVE_CONSTRUCTOR.invoke(onUserCreateStub, onUserUpdateStub, onUserDestroyStub, onConsoleCommandStub, onTextEntryCompleteStub);
            methods = new OlcMethods();
            init(width, height, pixelWidth, pixelHeight, fullScreen, vSync, cohesion, realWindow);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void init(int width, int height, int pixelWidth, int pixelHeight, boolean fullScreen, boolean vSync, boolean cohesion, boolean realWindow) {
        log.info("Initializing PixelGameEngine...");
        var result = PixelgameEngineReturnCode.fromCode(methods.construct.invoke(instancePtr, width, height, pixelWidth, pixelHeight, fullScreen, vSync, cohesion, realWindow));
        log.info("PixelGameEngineReturnCode: {}", result);
    }

    @Override
    public final void clean() {
        log.info("Cleaning up PixelGameEngine...");
        GAME_ENGINE_DESTROY.invoke(instancePtr);
    }

    /**
     * Called once on application startup, use to load your resources
     *
     * @return True if initialisation was successful
     */
    @NativeCallCandidate
    public abstract boolean onUserCreate();

    /**
     * Called every frame, and provides you with a time per frame value
     *
     * @param delta time since last frame
     * @return True if the app should keep running
     */
    @NativeCallCandidate
    public abstract boolean onUserUpdate(float delta);

    /**
     * Called once on application termination, so you can be one clean coder
     */
    @NativeCallCandidate
    public boolean onUserDestroy() {
        clean();
        return true;
    }


    @NativeCallCandidate
    final boolean onConsoleCommand(MemorySegment command) {
        return onConsoleCommand(Util.cString(command));
    }


    @NativeCallCandidate
    protected final void onTextEntryComplete(MemorySegment text) {
        onTextEntryComplete(Util.cString(text));
    }

    /**
     * Called when a console command is executed
     *
     * @param command Command line
     * @return false if the pixelGameEngine should check with its own internal commands,
     * or true if it was handled by this method
     */
    protected boolean onConsoleCommand(String command) {
        return false;
    }

    /**
     * Called when a text entry is confirmed with "enter" key
     *
     * @param text The text
     */
    protected void onTextEntryComplete(String text) {
    }


    /**
     * Starts the engine and opens the window.
     *
     * @implNote Blocks the current Thread until the pixel game engine finishes.
     */
    public final void start() {
        NativeFunction<Integer> fn = new NativeFunction<>("start", ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        int returnCode = fn.invoke(instancePtr);
        if (returnCode == 1) {
            log.info("Successfully started Pixel Game Engine");
        } else {
            throw new RuntimeException("Failed to start Pixel Game Engine");
        }
    }

    public final void consoleWriteln(String text) {
        consoleWrite(text + "\n");
    }

    public final void consoleWrite(String text) {
        methods.printToConsole.invoke(instancePtr, arena.allocateFrom(text));
    }

    /**
     * @return Whether the application window is focused
     */
    public final boolean isFocussed() {
        return methods.isFocused.invoke(instancePtr);
    }

    /**
     * Draws a single Pixel at the given position in the given color.
     *
     * @param pos The position to draw to
     * @param p   Color of the Pixel
     * @return True if the drawing was successful, false otherwise
     */
    public final boolean draw(Vector2D<Integer> pos, Pixel p) {
        return draw(pos.getX(), pos.getY(), p);
    }

    /**
     * Draws a single Pixel at the given position in the given color.
     *
     * @param x The x position to draw to
     * @param y The y position to draw to
     * @param p Color of the Pixel
     * @return True if the drawing was successful, false otherwise
     */
    public final boolean draw(int x, int y, Pixel p) {
        return methods.draw.invoke(instancePtr, x, y, p.getRGBA());
    }

    public final void drawLine(int x1, int y1, int x2, int y2, Pixel p, int pattern) {
        methods.drawLine.invoke(instancePtr, x1, y1, x2, y2, p.getRGBA(), pattern);
    }

    public final void drawRect(int x, int y, int width, int height, Pixel p) {
        methods.drawRect.invoke(instancePtr, x, y, width, height, p.getRGBA());
    }

    public final void fillRect(int x, int y, int width, int height, Pixel p) {
        methods.fillRect.invoke(instancePtr, x, y, width, height, p.getRGBA());
    }

    /**
     * Retrieves the state of a given key at this current Frame.
     *
     * @param k Key
     * @return the state of the given key at this current Frame
     */
    public final HWButton getKey(KeyBoardKey k) {
        return methods.getKey.invokeObj(HWButton::new, instancePtr, k.ordinal());
    }

    /**
     * Retrieves the state of a given mouse key at this current Frame.
     *
     * @param k Key
     * @return the state of the given mouse key at this current Frame
     */
    public final HWButton getKey(MouseKey k) {
        return methods.getMouseBtn.invokeObj(HWButton::new, instancePtr, k.ordinal());
    }

    /**
     * @return Position of the mouse
     */
    public final Vector2D<Integer> getMousePos() {
        return methods.getMousePos.invokeObj(IntVector2D::new, instancePtr);
    }

    /**
     * @return Position of the mouse in relation to the window
     */
    public final Vector2D<Integer> getWindowMousePos() {
        return methods.getWindowMousePos.invokeObj(IntVector2D::new, instancePtr);
    }

    /**
     * Retrieves the mouse wheel movement.
     *
     * @return a value less than 0 if scrolling down, a value greater than 0 if scrolling up,
     * or 0 if there is no scrolling activity
     */
    public final int getMouseWheel() {
        return methods.getMouseWheel.invoke(instancePtr);
    }

    /**
     * Sets the screen size to the specified dimensions.
     *
     * @param width  the new width of the screen in pixels
     * @param height the new height of the screen in pixels
     */
    public final void setScreenSize(int width, int height) {
        methods.setScreenSize.invoke(instancePtr, width, height);
    }

    /**
     * Draws a string at the specified position with the given color and default scale (1).
     *
     * @param x     the x-coordinate of the string's starting position
     * @param y     the y-coordinate of the string's starting position
     * @param text  the string to be drawn
     * @param color the color of the text
     */
    public final void drawString(int x, int y, String text, Pixel color) {
        drawString(x, y, text, color, 1);
    }

    /**
     * Draws a string at the specified position with the given color and scale.
     *
     * @param x     the x-coordinate of the string's starting position
     * @param y     the y-coordinate of the string's starting position
     * @param text  the string to be drawn
     * @param color the color of the text
     * @param scale the scaling factor for the text (1 = original size)
     */
    public final void drawString(int x, int y, String text, Pixel color, int scale) {
        MemorySegment cString = arena.allocateFrom(text);
        methods.drawString.invoke(instancePtr, x, y, cString, color.getRGBA(), scale);
    }

    /**
     * Draws a circle outline with the specified radius and color, using a full mask (0xFF).
     *
     * @param x      the x-coordinate of the circle's center
     * @param y      the y-coordinate of the circle's center
     * @param radius the radius of the circle
     * @param color  the color of the circle
     */
    public final void drawCircle(int x, int y, int radius, Pixel color) {
        methods.drawCircle.invoke(instancePtr, x, y, radius, color.getRGBA(), 0xFF);
    }

    /**
     * Draws a circle outline with the specified radius, color, and mask pattern.
     * The mask parameter controls which segments of the circle are drawn using an 8-bit pattern:
     * Each bit represents an octant of the circle (0-7 starting from the right and moving clockwise).
     * Setting a bit to 1 draws that octant, while 0 leaves it blank.
     * <p>
     * For example:
     * - 0xFF (255) draws all octants (full circle)
     * - 0x0F (15) draws the right half of the circle
     * - 0x03 (3) draws only the bottom-right and top-right quadrants
     *
     * @param x      the x-coordinate of the circle's center
     * @param y      the y-coordinate of the circle's center
     * @param radius the radius of the circle
     * @param color  the color of the circle
     * @param mask   an 8-bit value (0-255) controlling which circle segments are drawn
     */
    //TODO:maybe replace "mask" with some kind of intuitive wrapper class to allow for something like SEMI_CIRCLE or something
    public final void drawCircle(int x, int y, int radius, Pixel color, int mask) {
        methods.drawCircle.invoke(instancePtr, x, y, radius, color.getRGBA(), mask);
    }

    /**
     * Fills a circle with the specified radius and color.
     *
     * @param x      the x-coordinate of the circle's center
     * @param y      the y-coordinate of the circle's center
     * @param radius the radius of the circle
     * @param color  the fill color of the circle
     */
    public final void fillCircle(int x, int y, int radius, Pixel color) {
        methods.fillCircle.invoke(instancePtr, x, y, radius, color.getRGBA());
    }

    public final Vector2D<Integer> getScreenPixelSize() {
        return methods.getScreenPixelSize.invokeObj(IntVector2D::new);
    }

    public final Vector2D<Integer> getScreenSize() {
        return methods.getScreenSize.invokeObj(IntVector2D::new);
    }

    /**
     * @param closeKey    Button that determines that it's time to close the console again
     * @param suspendTime whether the Application should halt while console is opened
     */
    public final void consoleShow(KeyBoardKey closeKey, boolean suspendTime) {
        methods.consoleShow.invoke(instancePtr, closeKey.ordinal(), suspendTime);
    }

    public final void consoleClear() {
        methods.consoleClear.invoke(instancePtr);
    }

    public final boolean isConsoleShowing() {
        return methods.isConsoleShowing.invoke(instancePtr);
    }

    /**
     * Toggle text entry mode with the default String {@code ""}
     *
     * @see #textEntryEnable(boolean, String)
     */
    public final void textEntryEnable(boolean enable) {
        textEntryEnable(enable, "");
    }

    /**
     * Enters Text Entry mode, allowing the user to enter text.
     * This text will not be displayed natively,
     * however it can be queried using {@link #textEntryGetString()} to be displayed manually.
     * <p>
     * Expected usage is:
     * A key is pressed and the game enters a “text entry mode”. All keyboard keys typed now add characters to the text entry mode input text.
     * When user is done typing they press enter to submit whatever was typed, triggering {@link #onTextEntryComplete(String)} with the user input
     *
     * @param enable      whether to enter (true) or exit (false) the text entry mode
     * @param initialText The initial text that should be inserted upon entering
     */
    public final void textEntryEnable(boolean enable, String initialText) {
        methods.textEntryEnable.invoke(instancePtr, enable, arena.allocateFrom(initialText));
    }

    public final String textEntryGetString() {
        return methods.textEntryGetString.invokeObj(Util::cString);
    }

    public final int textEntryGetCursor() {
        return methods.textEntryGetCursor.invoke(instancePtr);
    }

    public final boolean isTextEntryEnabled() {
        return methods.isTextEntryEnabled.invoke(instancePtr);
    }

    public final void drawSprite(int x, int y, Sprite sprite, int scale, FlipMode mode) {
        methods.drawSprite.invoke(instancePtr, x, y, sprite.getSpritePtr(), scale, (byte) mode.ordinal());
    }

    /**
     * @see #drawPartialSprite(int, int, Sprite, int, int, int, int, int, FlipMode)
     */
    public final void drawPartialSprite(int x, int y, Sprite sprite, int ox, int oy, int w, int h, int scale) {
        drawPartialSprite(x, y, sprite, ox, oy, w, h, scale, FlipMode.NONE);
    }

    /**
     * @param x      X Coordinate in the screen
     * @param y      Y Coordinate in the screen
     * @param sprite The sprite to draw from
     * @param ox     The X coordinate where the sprite part begins
     * @param oy     The Y coordinate where the sprite part begins
     * @param w      The Width of the sprite frame
     * @param h      The height of the sprite frame
     * @param scale  the scale to draw it in
     * @param mode   Optional Flip mode to mirror the sprite.
     */
    public final void drawPartialSprite(int x, int y, Sprite sprite, int ox, int oy, int w, int h, int scale, FlipMode mode) {
        methods.drawPartialSprite.invoke(instancePtr, x, y, sprite.getSpritePtr(), scale, ox, oy, w, h, (byte) mode.ordinal());
    }

    public final void setPixelMode(PixelMode mode) {
        methods.setPixelMode.invoke(instancePtr, mode.ordinal());
    }

    public final void drawDecal(Vector2D<Float> pos, Decal decal) {
        drawDecal(pos, decal, FloatVector2D.ONE, Pixel.WHITE);
    }

    /**
     * // Draws a whole decal, with optional scale and tinting
     *
     * @param pos   Position in screen/window
     * @param decal Decal
     * @param scale Scaling option
     * @param tint  Tint
     */
    public final void drawDecal(Vector2D<Float> pos, Decal decal, Vector2D<Float> scale, Pixel tint) {
        methods.drawDecal.invoke(instancePtr, pos.toPtr(), decal.getPtr(), scale.toPtr(), tint.toPtr());
    }

    /**
     * @param pos        Position to draw at in the screen.
     * @param decal      The decal object
     * @param sourcePos  The position within the decal to start drawing
     * @param sourceSize The Size (Width/Height) of the rectangle to draw
     */
    public void drawPartialDecal(Vector2D<Float> pos, Decal decal, Vector2D<Float> sourcePos, Vector2D<Float> sourceSize) {
        drawPartialDecal(pos, decal, sourcePos, sourceSize, FloatVector2D.ONE, Pixel.WHITE);
    }

    /**
     * Draws a region of a decal, with optional scale and tinting
     *
     * @param pos        Position to draw at in the screen.
     * @param decal      The decal object
     * @param sourcePos  The position within the decal to start drawing
     * @param sourceSize The Size (Width/Height) of the rectangle to draw
     * @param scale      The scale to multiply the size with
     * @param tint       Tint of the Decal
     */
    public void drawPartialDecal(Vector2D<Float> pos, Decal decal, Vector2D<Float> sourcePos, Vector2D<Float> sourceSize, Vector2D<Float> scale, Pixel tint) {
        methods.drawPartialDecal.invoke(instancePtr, pos.toPtr(), decal.getPtr(), sourcePos.toPtr(), sourceSize.toPtr(), scale.toPtr(), tint.toPtr());
    }

    /**
     * Draws a decal using an explicit list of vertices, UV coordinates and per‑vertex colours.
     * <p>
     * This directly maps to {@code DrawExplicitDecal()} in the native engine and allows complete control over the quad.
     * It is the equivalent of drawing a custom mesh – handy for skeletal animation, isometric billboards, etc.
     *
     * @param decal     the texture‐backed decal to render
     * @param positions array of screen‑space vertices (must be length ≥ 3, typically 4)
     * @param uvs       array of UV coordinates in texture space, one per vertex
     * @param colors    array of {@code Pixel}s providing per‑vertex tinting; if you need a flat tint, pass the same colour for every vertex
     * @throws IllegalArgumentException if the supplied arrays are not all the same length
     */
    public final void drawExplicitDecal(Decal decal, Vector2D<Float>[] positions,
                                        Vector2D<Float>[] uvs, Pixel[] colors) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        MemorySegment uvArray = MemUtil.toArrayPtr(arena, uvs);
        MemorySegment colorArray = MemUtil.toArrayPtr(arena, colors);
        methods.drawExplicitDecal.invoke(instancePtr, decal.getPtr(), posArray, uvArray, colorArray, positions.length);
    }

    /**
     * Draws a decal warped through a perspective transform defined by four destination vertices.
     * <p>
     * Maps to {@code DrawWarpedDecal()} – ideal for pseudo‑3D billboards or texture‑mapped quads that need to be skewed.
     *
     * @param decal     the decal to render
     * @param positions array of exactly four destination vertices (top‑left, top‑right, bottom‑right, bottom‑left)
     * @param tint      overall colour tint; set to {@code Pixel.WHITE} for no tint
     */
    public final void drawWarpedDecal(Decal decal, Vector2D<Float>[] positions, Pixel tint) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        methods.drawWarpedDecal.invoke(instancePtr, decal.getPtr(), posArray, tint.toPtr());
    }

    /**
     * Draws a sub‑rectangle of a decal (defined in texture space) warped to four screen‑space vertices.
     *
     * @param decal      the source decal
     * @param positions  four destination vertices defining the warp
     * @param sourcePos  top‑left corner in texture space (pixels)
     * @param sourceSize width × height in texture space (pixels)
     * @param tint       overall colour tint, applied after the per‑pixel colour is fetched
     */
    public final void drawPartialWarpedDecal(Decal decal, Vector2D<Float>[] positions,
                                             Vector2D<Float> sourcePos, Vector2D<Float> sourceSize,
                                             Pixel tint) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        methods.drawPartialWarpedDecal.invoke(instancePtr, decal.getPtr(), posArray,
                sourcePos.toPtr(), sourceSize.toPtr(),
                tint.toPtr());
    }

    /**
     * Draws a decal rotated around an arbitrary centre point.
     *
     * @param pos    top‑left position of the decal before rotation
     * @param decal  the decal to render
     * @param angle  rotation in radians (clockwise, positive‑Y down)
     * @param center pivot within the decal (relative to {@code pos}) around which to rotate
     * @param scale  scaling factor applied before rotation (1,1 = original size)
     * @param tint   overall tint applied to the decal
     */
    public final void drawRotatedDecal(Vector2D<Float> pos, Decal decal, float angle,
                                       Vector2D<Float> center, Vector2D<Float> scale,
                                       Pixel tint) {
        methods.drawRotatedDecal.invoke(instancePtr, pos.toPtr(), decal.getPtr(), angle,
                center.toPtr(), scale.toPtr(), tint.toPtr());
    }

    /**
     * Draws a sub‑region of a decal with rotation and scaling.
     *
     * @param pos        destination of the unrotated top‑left corner
     * @param decal      the decal to draw from
     * @param angle      rotation in radians (clockwise)
     * @param center     pivot point for rotation, relative to {@code pos}
     * @param sourcePos  top‑left corner inside the decal’s texture
     * @param sourceSize width × height of the region inside the decal
     * @param scale      scaling factors applied before rotation
     * @param tint       overall tint
     */
    public final void drawPartialRotatedDecal(Vector2D<Float> pos, Decal decal, float angle,
                                              Vector2D<Float> center, Vector2D<Float> sourcePos,
                                              Vector2D<Float> sourceSize, Vector2D<Float> scale,
                                              Pixel tint) {
        methods.drawPartialRotatedDecal.invoke(instancePtr, pos.toPtr(), decal.getPtr(), angle,
                center.toPtr(), sourcePos.toPtr(),
                sourceSize.toPtr(), scale.toPtr(),
                tint.toPtr());
    }

    /**
     * Renders a string using the engine’s bitmap font as a decal, preserving sub‑pixel positioning.
     *
     * @param pos   screen position
     * @param text  UTF‑8 text to display (limited to glyphs available in the default font)
     * @param color tint to apply (per glyph)
     * @param scale font scaling; values &lt;1 shrink, values &gt;1 enlarge
     */
    public final void drawStringDecal(Vector2D<Float> pos, String text, Pixel color,
                                      Vector2D<Float> scale) {
        MemorySegment cString = arena.allocateFrom(text);
        methods.drawStringDecal.invoke(instancePtr, pos.toPtr(), cString, color.toPtr(), scale.toPtr());
    }

    /**
     * Renders proportional (variable‑width) text as a decal.
     * Identical to {@link #drawStringDecal} but uses the proportional font table in the engine.
     */
    public final void drawStringPropDecal(Vector2D<Float> pos, String text, Pixel color,
                                          Vector2D<Float> scale) {
        MemorySegment cString = arena.allocateFrom(text);
        methods.drawStringPropDecal.invoke(instancePtr, pos.toPtr(), cString, color.toPtr(), scale.toPtr());
    }

    /**
     * Draws an un‑filled axis‑aligned rectangle.
     */
    public final void drawRectDecal(Vector2D<Float> pos, Vector2D<Float> size, Pixel color) {
        methods.drawRectDecal.invoke(instancePtr, pos.toPtr(), size.toPtr(), color.toPtr());
    }

    /**
     * Draws a filled axis‑aligned rectangle.
     */
    public final void fillRectDecal(Vector2D<Float> pos, Vector2D<Float> size, Pixel color) {
        methods.fillRectDecal.invoke(instancePtr, pos.toPtr(), size.toPtr(), color.toPtr());
    }

    /**
     * Draws a filled rectangle with a 4‑corner gradient.
     * The engine will smoothly interpolate the colours across the quad.
     */
    public final void gradientFillRectDecal(Vector2D<Float> pos, Vector2D<Float> size,
                                            Pixel colorTL, Pixel colorBL,
                                            Pixel colorBR, Pixel colorTR) {
        methods.gradientFillRectDecal.invoke(instancePtr, pos.toPtr(), size.toPtr(),
                colorTL.toPtr(), colorBL.toPtr(),
                colorBR.toPtr(), colorTR.toPtr());
    }

    public final void gradientLineDecal(Vector2D<Float> posA, Vector2D<Float> posB, Pixel startColor, Pixel endColor) {
        methods.gradientLineDecal.invoke(instancePtr, posA.toPtr(), posB.toPtr(), startColor.toPtr(), endColor.toPtr());
    }

    /**
     * Draws a filled triangle.
     */
    public final void fillTriangleDecal(Vector2D<Float> p0, Vector2D<Float> p1,
                                        Vector2D<Float> p2, Pixel color) {
        methods.fillTriangleDecal.invoke(instancePtr, p0.toPtr(), p1.toPtr(), p2.toPtr(), color.toPtr());
    }

    /**
     * Draws a filled triangle with per‑vertex gradient colours.
     */
    public final void gradientTriangleDecal(Vector2D<Float> p0, Vector2D<Float> p1,
                                            Vector2D<Float> p2, Pixel c0, Pixel c1,
                                            Pixel c2) {
        methods.gradientTriangleDecal.invoke(instancePtr, p0.toPtr(), p1.toPtr(), p2.toPtr(),
                c0.toPtr(), c1.toPtr(), c2.toPtr());
    }

    /**
     * Draws a polygon decal with a uniform tint colour.
     *
     * @param decal     decal to sample from
     * @param positions vertex positions (convex or concave – the engine will triangulate internally)
     * @param uvs       UV coordinates per vertex
     * @param tint      overall tint applied after texturing
     */
    public final void drawPolygonDecal(Decal decal, Vector2D<Float>[] positions,
                                       Vector2D<Float>[] uvs, Pixel tint) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        MemorySegment uvArray = MemUtil.toArrayPtr(arena, uvs);
        methods.drawPolygonDecal.invoke(instancePtr, decal.getPtr(), posArray, uvArray, tint.toPtr());
    }

    /**
     * Same as {@link #drawPolygonDecal(Decal, Vector2D[], Vector2D[], Pixel)} but supplies a per‑vertex depth buffer.
     */
    public final void drawPolygonDecal(Decal decal, Vector2D<Float>[] positions,
                                       float[] depths, Vector2D<Float>[] uvs,
                                       Pixel tint) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        MemorySegment depthArray = MemUtil.toArrayPtr(arena, depths);
        MemorySegment uvArray = MemUtil.toArrayPtr(arena, uvs);
        methods.drawPolygonDecalWithDepth.invoke(instancePtr, decal.getPtr(), posArray,
                depthArray, uvArray, tint.toPtr());
    }

    /**
     * Polygon decal with per‑vertex colours (no depth).
     */
    public final void drawPolygonDecal(Decal decal, Vector2D<Float>[] positions,
                                       Vector2D<Float>[] uvs, Pixel[] colors) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        MemorySegment uvArray = MemUtil.toArrayPtr(arena, uvs);
        MemorySegment colorArray = MemUtil.toArrayPtr(arena, colors);
        methods.drawPolygonDecalWithColors.invoke(instancePtr, decal.getPtr(), posArray,
                uvArray, colorArray);
    }

    /**
     * Polygon decal with per‑vertex colours <em>and</em> an overall tint.
     */
    public final void drawPolygonDecal(Decal decal, Vector2D<Float>[] positions,
                                       Vector2D<Float>[] uvs, Pixel[] colors,
                                       Pixel tint) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        MemorySegment uvArray = MemUtil.toArrayPtr(arena, uvs);
        MemorySegment colorArray = MemUtil.toArrayPtr(arena, colors);
        methods.drawPolygonDecalWithColorsAndTint.invoke(instancePtr, decal.getPtr(), posArray,
                uvArray, colorArray, tint.toPtr());
    }

    /**
     * Full‑fat polygon draw: per‑vertex depth, colour, plus global tint.
     */
    public final void drawPolygonDecal(Decal decal, Vector2D<Float>[] positions,
                                       float[] depths, Vector2D<Float>[] uvs,
                                       Pixel[] colors, Pixel tint) {
        MemorySegment posArray = MemUtil.toArrayPtr(arena, positions);
        MemorySegment depthArray = MemUtil.toArrayPtr(arena, depths);
        MemorySegment uvArray = MemUtil.toArrayPtr(arena, uvs);
        MemorySegment colorArray = MemUtil.toArrayPtr(arena, colors);
        methods.drawPolygonDecalWithDepthAndColorsAndTint.invoke(instancePtr, decal.getPtr(), posArray,
                depthArray, uvArray,
                colorArray, tint.toPtr());
    }

    /**
     * Draws a single anti‑aliased line between {@code pos1} and {@code pos2}.
     */
    public final void drawLineDecal(Vector2D<Float> pos1, Vector2D<Float> pos2, Pixel color) {
        methods.drawLineDecal.invoke(instancePtr, pos1.toPtr(), pos2.toPtr(), color.toPtr());
    }

    /**
     * Renders a rotated string using the bitmap font.
     * <p>
     * Useful for dial gauges, scrolling credits, and other HUD elements that need orientation.
     */
    public final void drawRotatedStringDecal(Vector2D<Float> pos, String text, float angle,
                                             Vector2D<Float> center, Pixel color,
                                             Vector2D<Float> scale) {
        MemorySegment cString = arena.allocateFrom(text);
        methods.drawRotatedStringDecal.invoke(instancePtr, pos.toPtr(), cString, angle,
                center.toPtr(), color.toPtr(), scale.toPtr());
    }

    /**
     * Proportional version of {@link #drawRotatedStringDecal}.
     */
    public final void drawRotatedStringPropDecal(Vector2D<Float> pos, String text, float angle,
                                                 Vector2D<Float> center, Pixel color,
                                                 Vector2D<Float> scale) {
        MemorySegment cString = arena.allocateFrom(text);
        methods.drawRotatedStringPropDecal.invoke(instancePtr, pos.toPtr(), cString, angle,
                center.toPtr(), color.toPtr(), scale.toPtr());
    }

    /**
     * Clears the entire frame buffer to {@code color}. Call once per frame, typically at the top of your render loop.
     */
    public final void clear(Pixel color) {
        methods.clear.invoke(instancePtr, color.toPtr());
    }

    /**
     * Sets the blending mode used for subsequent decal draws.
     */
    public final void setDecalMode(DecalMode mode) {
        methods.setDecalMode.invoke(instancePtr, mode.ordinal());
    }

    /**
     * Sets the vertex structure (position‑only, position+uv, etc.) that the following decal calls will assume.
     */
    public final void setDecalStructure(DecalStructure structure) {
        methods.setDecalStructure.invoke(instancePtr, structure.ordinal());
    }

    /**
     * @param pos  Position of the top left corner on the screen
     * @param size Width and Height of the window
     */
    public void resize(Vector2D<Integer> pos, Vector2D<Integer> size) {
        methods.resize.invoke(instancePtr, pos.toPtr(), size.toPtr());
    }

    /**
     * @return The position of the top left corner of the window.
     */
    public Vector2D<Integer> getWindowPos() {
        return methods.getWindowPos.invokeObj(IntVector2D::new, instancePtr);
    }

    /**
     * @return The size of the window.
     */
    public Vector2D<Integer> getWindowSize() {
        return methods.getWindowSize.invokeObj(IntVector2D::new, instancePtr);
    }

    public void setWindowTitle(String title) {
        methods.setWindowTitle.invoke(instancePtr, arena.allocateFrom(title));
    }

    /**
     * @return the current framerate
     */
    public int getFramerate() {
        return methods.getFramerate.invoke(instancePtr);
    }
}
