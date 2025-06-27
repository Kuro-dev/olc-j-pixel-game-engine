package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.SneakyThrows;
import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.IntVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;


public abstract class PixelGameEngine implements AutoCloseable {
    static final Linker LINKER = Linker.nativeLinker();
    static final SymbolLookup LIB = SymbolLookup.loaderLookup();
    private final Arena arena;

    private final OlcMethods methods;

    public PixelGameEngine() {
        this(500, 500);
    }

    @SneakyThrows
    public PixelGameEngine(int width, int height) {
        this.arena = Arena.ofShared();
        methods = new OlcMethods();
        init(width, height);
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
                        ValueLayout.ADDRESS)
        );
        //statuscode 0, 1, 2
        var onUserCreateStub = EngineInitialiser.createOnUserCreateStub(LINKER, arena, this);
        var onUserUpdateStub = EngineInitialiser.createOnUserUpdateStub(LINKER, arena, this);
        var onUserDestroyStub = EngineInitialiser.createOnUserDestroyStub(LINKER, arena, this);
        int statusCode = (int) createInstance.invokeExact(width, height, onUserCreateStub, onUserUpdateStub, onUserDestroyStub);
        switch (NativeStatusCode.ofCode(statusCode)) {
            case SUCCESS -> System.out.println("Successfully initialised Pixel Game Engine");
            case FAIL -> System.out.println("Failed to initialise Pixel Game Engine");
            case INSTANCE_ALREADY_EXISTS -> System.out.println("Pixelgame engine instance already exists");
        }
    }


    // Callback methods called from native code
    @NativeCallCandidate
    public abstract boolean onUserCreate();

    @NativeCallCandidate
    public abstract boolean onUserUpdate(float delta);

    /**
     * Called once on application termination, so you can be one clean coder
     */
    @NativeCallCandidate
    public boolean onUserDestroy() {
        return true;
    }


    @Override
    public void close() {
        arena.close();
    }

    /**
     * { FAIL = 0, OK = 1, NO_FILE = -1 };
     */
    @SneakyThrows
    public void start() {
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
    public boolean isFocussed() {
        return methods.isFocused().invoke();
    }

    /**
     * Draws a single Pixel at the given position in the given color.
     *
     * @param pos The position to draw to
     * @param p   Color of the Pixel
     * @return True if the drawing was successful, false otherwise
     */
    public boolean draw(IntVector2D pos, Pixel p) {
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
    public boolean draw(int x, int y, Pixel p) {
        return methods.draw().invoke(x, y, p.getRGBA());
    }

    /**
     * Retrieves the state of a given key at this current Frame.
     *
     * @param k Key
     * @return the state of the given key at this current Frame
     */
    public HWButton getKey(KeyBoardKey k) {
        return methods.getKey().invoke(k.ordinal());
    }

    /**
     * Retrieves the state of a given mouse key at this current Frame.
     *
     * @param k Key
     * @return the state of the given mouse key at this current Frame
     */
    public HWButton getKey(MouseKey k) {
        return methods.getMouseBtn().invokeObj(HWButton::new, k.ordinal());
    }

    /**
     * @return Position of the mouse
     */
    public Vector2D<Integer> getMousePos() {
        return methods.getMousePos().invokeObj(IntVector2D::new);
    }

    /**
     * @return Position of the mouse in relation to the window
     */
    @SneakyThrows
    public Vector2D<Integer> getWindowMousePos() {
        return methods.getWindowMousePos().invokeObj(IntVector2D::new);
    }

    /**
     * Retrieves the mouse wheel movement.
     *
     * @return a value less than 0 if scrolling down, a value greater than 0 if scrolling up,
     * or 0 if there is no scrolling activity
     */
    public int getMouseWheel() {
        return methods.getMouseWheel().invoke();
    }

    /**
     * Sets the screen size to the specified dimensions.
     *
     * @param width  the new width of the screen in pixels
     * @param height the new height of the screen in pixels
     */
    public void setScreenSize(int width, int height) {
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
    public void drawString(int x, int y, String text, Pixel color) {
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
    public void drawString(int x, int y, String text, Pixel color, int scale) {
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
    public void drawCircle(int x, int y, int radius, Pixel color) {
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
    public void drawCircle(int x, int y, int radius, Pixel color, int mask) {
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
    public void fillCircle(int x, int y, int radius, Pixel color) {
        methods.fillCircle().invoke(x, y, radius, color.getRGBA());
    }
}
