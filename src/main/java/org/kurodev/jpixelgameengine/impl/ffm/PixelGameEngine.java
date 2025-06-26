package org.kurodev.jpixelgameengine.impl.ffm;

import lombok.SneakyThrows;
import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;


public abstract class PixelGameEngine {
    static final Linker LINKER = Linker.nativeLinker();
    static final SymbolLookup LIB = SymbolLookup.loaderLookup();
    private final Arena arena;

    private final OlcMethods methods = new OlcMethods();

    public PixelGameEngine() {
        this(500, 500);
    }

    @SneakyThrows
    public PixelGameEngine(int width, int height) {
        this.arena = Arena.ofConfined();
        init(width, height);

    }


    private void init(int width, int height) throws Throwable {
        System.out.println("Initializing FFMPixelGameEngineWrapper");
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
    public abstract boolean onUserCreate();

    public abstract boolean onUserUpdate(float delta);

    /**
     * Called once on application termination, so you can be one clean coder
     */
    public boolean onUserDestroy() {
        return true;
    }

    @NativeCallCandidate
    boolean onUserDestroyIntl() {
        boolean result = onUserDestroy();
        if (result) {
            arena.close(); //cleanup arena
        }
        return result;
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

    public boolean isFocussed() {
        return methods.isFocused().invoke();
    }

    public boolean draw(Vector2D<? extends Number> pos, Pixel p) {
        return draw(pos.getX().intValue(), pos.getY().intValue(), p);
    }

    public boolean draw(int x, int y, Pixel p) {
        return methods.draw().invoke(x, y, p.getRGBA());
    }

    public HWButton getKey(KeyBoardKey k) {
        return methods.getKey().invoke(k.ordinal());
    }

    public HWButton getKey(MouseKey k) {
        return methods.getMouseBtn().invoke(k.ordinal());
    }

    public Vector2D<Integer> getMousePos() {
        return methods.getMousePos().invoke();
    }

    public Vector2D<Integer> getWindowMousePos() {
        return methods.getWindowMousePos().invoke();
    }

    /**
     * @return a value < 0 if scrolling down, a value > 0 if scrolling up, or 0 if not scrolling
     */
    public int getMouseWheel() {
        return methods.getMouseWheel().invoke();
    }

    public void setScreenSize(int width, int height) {
        methods.setScreenSize().invoke(width, height);
    }
}
