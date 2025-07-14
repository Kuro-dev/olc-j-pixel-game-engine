package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.SneakyThrows;
import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;
import org.kurodev.jpixelgameengine.impl.ui.UIManager;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Due to how FFM works this class must be inherited by a Named (NOT ANONYMOUS) class. Otherwise, it will fail.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public abstract class PixelGameEngine {
    private final UIManager uiManager;

    static final Linker LINKER = Linker.nativeLinker();
    static final SymbolLookup LIB = SymbolLookup.loaderLookup();
    private final Arena arena;

    private final OlcMethods methods;

    public PixelGameEngine() {
        this(500, 500);
    }

    @SneakyThrows
    public PixelGameEngine(int width, int height) {
        this.arena = Arena.ofAuto();
        methods = new OlcMethods();
        init(width, height);
        uiManager = new UIManager(this);
    }

    private void init(int width, int height) throws Throwable {
        System.out.println("Initializing PixelGameEngine...");
        MethodHandle createInstance = LINKER.downcallHandle(
                LIB.find("createGameEngineInstance").orElseThrow(),
                FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,
                        ValueLayout.JAVA_INT,
                        ValueLayout.JAVA_INT,
                        ValueLayout.ADDRESS,
                        ValueLayout.ADDRESS,
                        ValueLayout.ADDRESS,
                        ValueLayout.ADDRESS,
                        ValueLayout.ADDRESS)
        );
        //statuscode 0, 1, 2
        var onUserCreateStub = EngineInitialiser.createOnUserCreateStub(LINKER, arena, this);
        var onUserUpdateStub = EngineInitialiser.createOnUserUpdateStub(LINKER, arena, this);
        var onUserDestroyStub = EngineInitialiser.createOnUserDestroyStub(LINKER, arena, this);
        var onConsoleCommandStub = EngineInitialiser.createOnConsoleCommandStub(LINKER, arena, this);
        var onTextEntryCompleteStub = EngineInitialiser.createTextEntryCompleteStub(LINKER, arena, this);
        int statusCode = (int) createInstance.invokeExact(
                width,
                height,
                onUserCreateStub,
                onUserUpdateStub,
                onUserDestroyStub,
                onConsoleCommandStub,
                onTextEntryCompleteStub);
        switch (NativeStatusCode.ofCode(statusCode)) {
            case SUCCESS -> System.out.println("Successfully initialised Pixel Game Engine");
            case FAIL -> System.out.println("Failed to initialise Pixel Game Engine");
            case INSTANCE_ALREADY_EXISTS -> System.out.println("Pixelgame engine instance already exists");
        }
    }

    public final UIManager getUIManager() {
        return uiManager;
    }

    /**
     * Called once on application startup, use to load your resources
     *
     * @return True if initialisation was successful
     */
    @NativeCallCandidate
    public abstract boolean onUserCreate();

    @NativeCallCandidate
    public final boolean onUserUpdateIntl(float delta) {
        boolean result = onUserUpdate(delta);
        if (uiManager.isEnabled()) {
            uiManager.draw();
        }
        return result;
    }

    /**
     * Called every frame, and provides you with a time per frame value
     *
     * @param delta time since last frame
     * @return True if the app should keep running
     */
    public abstract boolean onUserUpdate(float delta);

    /**
     * Called once on application termination, so you can be one clean coder
     */
    @NativeCallCandidate
    public boolean onUserDestroy() {
        return true;
    }


    @NativeCallCandidate
    protected final boolean onConsoleCommand(MemorySegment command) {
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
    @SneakyThrows
    public final void start() {
        NativeFunction<Integer> fn = new NativeFunction<>("start", ValueLayout.JAVA_INT);
        int returnCode = fn.invoke();
        if (returnCode == 1) {
            System.out.println("Successfully started Pixel Game Engine");
        } else {
            throw new RuntimeException("Failed to start Pixel Game Engine");
        }

    }

    /**
     * @return Whether the application window is focused
     */
    public final boolean isFocussed() {
        return methods.isFocused().invoke();
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
        return methods.draw().invoke(x, y, p.getRGBA());
    }

    public final void drawLine(int x1, int y1, int x2, int y2, Pixel p, int pattern) {
        methods.drawLine().invoke(x1, y1, x2, y2, p.getRGBA(), pattern);
    }

    public final void drawRect(int x, int y, int width, int height, Pixel p) {
        methods.drawRect().invoke(x, y, width, height, p.getRGBA());
    }

    public final void fillRect(int x, int y, int width, int height, Pixel p) {
        methods.fillRect().invoke(x, y, width, height, p.getRGBA());
    }

    /**
     * Retrieves the state of a given key at this current Frame.
     *
     * @param k Key
     * @return the state of the given key at this current Frame
     */
    public final HWButton getKey(KeyBoardKey k) {
        return methods.getKey().invokeObj(HWButton::new, k.ordinal());
    }

    /**
     * Retrieves the state of a given mouse key at this current Frame.
     *
     * @param k Key
     * @return the state of the given mouse key at this current Frame
     */
    public final HWButton getKey(MouseKey k) {
        return methods.getMouseBtn().invokeObj(HWButton::new, k.ordinal());
    }

    /**
     * @return Position of the mouse
     */
    public final Vector2D<Integer> getMousePos() {
        return methods.getMousePos().invokeObj(IntVector2D::new);
    }

    /**
     * @return Position of the mouse in relation to the window
     */
    @SneakyThrows
    public final Vector2D<Integer> getWindowMousePos() {
        return methods.getWindowMousePos().invokeObj(IntVector2D::new);
    }

    /**
     * Retrieves the mouse wheel movement.
     *
     * @return a value less than 0 if scrolling down, a value greater than 0 if scrolling up,
     * or 0 if there is no scrolling activity
     */
    public final int getMouseWheel() {
        return methods.getMouseWheel().invoke();
    }

    /**
     * Sets the screen size to the specified dimensions.
     *
     * @param width  the new width of the screen in pixels
     * @param height the new height of the screen in pixels
     */
    public final void setScreenSize(int width, int height) {
        methods.setScreenSize().invoke(width, height);
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
        methods.drawString().invoke(x, y, cString, color.getRGBA(), scale);
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
        methods.drawCircle().invoke(x, y, radius, color.getRGBA(), 0xFF);
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
        methods.drawCircle().invoke(x, y, radius, color.getRGBA(), mask);
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
        methods.fillCircle().invoke(x, y, radius, color.getRGBA());
    }

    /**
     * @param closeKey    Button that determines that it's time to close the console again
     * @param suspendTime whether the Application should halt while console is opened
     */
    public final void consoleShow(KeyBoardKey closeKey, boolean suspendTime) {
        methods.consoleShow().invoke(closeKey.ordinal(), suspendTime);
    }

    public final void consoleClear() {
        methods.consoleClear().invoke();
    }

    public final boolean isConsoleShowing() {
        return methods.isConsoleShowing().invoke();
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
        methods.textEntryEnable().invoke(enable, arena.allocateFrom(initialText));
    }

    public final String textEntryGetString() {
        return methods.textEntryGetString().invokeObj(Util::cString);
    }

    public final int textEntryGetCursor() {
        return methods.textEntryGetCursor().invoke();
    }

    public final boolean isTextEntryEnabled() {
        return methods.isTextEntryEnabled().invoke();
    }
}
