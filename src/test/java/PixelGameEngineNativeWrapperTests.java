import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kurodev.jpixelgameengine.impl.ffm.BatchedPixelGameEngine;
import org.kurodev.jpixelgameengine.gfx.CullMode;
import org.kurodev.jpixelgameengine.gfx.LineClipResult;
import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.gfx.decal.DecalMode;
import org.kurodev.jpixelgameengine.gfx.decal.DecalPatch;
import org.kurodev.jpixelgameengine.gfx.decal.DecalStructure;
import org.kurodev.jpixelgameengine.gfx.sprite.FlipMode;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.kurodev.jpixelgameengine.gfx.sprite.SpritePatch;
import org.kurodev.jpixelgameengine.impl.PixelgameEngineReturnCode;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class PixelGameEngineNativeWrapperTests {

    private static void withEngine(EngineAction action) {
        TestEngine engine = new TestEngine();
        action.run(engine);
        engine.clean();
    }

    private static void withStartedEngine(EngineAction action) {
        withStartEngine(action);
    }

    private static void withStartEngine(EngineAction action) {
        TestEngine engine = new TestEngine();
        AtomicReference<Throwable> startFailure = new AtomicReference<>();
        Thread engineThread = new Thread(() -> {
            try {
                engine.start();
            } catch (Throwable throwable) {
                startFailure.set(throwable);
                engine.markStarted();
            }
        }, "test-pixel-game-engine");

        engineThread.start();
        try {
            engine.awaitStarted();
            rethrowStartFailure(startFailure.get());
            action.run(engine);
        } finally {
            engine.stop();
            joinEngineThread(engineThread);
            rethrowStartFailure(startFailure.get());
        }

    }

    private static void withStartedBatchedEngine(BatchedEngineAction action) {
        BatchedTestEngine engine = new BatchedTestEngine();
        AtomicReference<Throwable> startFailure = new AtomicReference<>();
        Thread engineThread = new Thread(() -> {
            try {
                engine.start();
            } catch (Throwable throwable) {
                startFailure.set(throwable);
                engine.markStarted();
            }
        }, "test-batched-pixel-game-engine");

        engineThread.start();
        try {
            engine.awaitStarted();
            engine.awaitFirstUpdate();
            rethrowStartFailure(startFailure.get());
            action.run(engine);
        } finally {
            engine.stop();
            joinEngineThread(engineThread);
            rethrowStartFailure(startFailure.get());
        }
    }

    private static void joinEngineThread(Thread engineThread) {
        try {
            engineThread.join(TimeUnit.SECONDS.toMillis(15));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for test engine to stop", e);
        }
        if (engineThread.isAlive()) {
            throw new RuntimeException("Timed out waiting for test engine to stop");
        }
    }

    private static void rethrowStartFailure(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        if (throwable instanceof Error error) {
            throw error;
        }
        throw new RuntimeException(throwable);
    }

    private static Vector2D<Float>[] trianglePoints() {
        return new FloatVector2D[]{
                new FloatVector2D(0.0f, 0.0f),
                new FloatVector2D(4.0f, 0.0f),
                new FloatVector2D(0.0f, 4.0f)};
    }

    private static Vector2D<Float>[] triangleUv() {
        return new FloatVector2D[]{
                new FloatVector2D(0.0f, 0.0f),
                new FloatVector2D(1.0f, 0.0f),
                new FloatVector2D(0.0f, 1.0f)};
    }

    private static Pixel[] triangleColors() {
        return new Pixel[]{Pixel.WHITE, Pixel.WHITE, Pixel.WHITE};
    }

    private static Vector2D<Float>[] quadPoints() {
        return new FloatVector2D[]{
                new FloatVector2D(0.0f, 0.0f),
                new FloatVector2D(8.0f, 0.0f),
                new FloatVector2D(8.0f, 8.0f),
                new FloatVector2D(0.0f, 8.0f)
        };
    }

    private static Vector2D<Float>[] quadUv() {
        return new FloatVector2D[]{
                new FloatVector2D(0.0f, 0.0f),
                new FloatVector2D(1.0f, 0.0f),
                new FloatVector2D(1.0f, 1.0f),
                new FloatVector2D(0.0f, 1.0f)
        };
    }

    private static Pixel[] quadColors() {
        return new Pixel[]{Pixel.WHITE, Pixel.WHITE, Pixel.WHITE, Pixel.WHITE};
    }

    private static float[] identityMatrix() {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };
    }

    @Test
    void inputAndUtilityWrappersResolve() {
        withEngine(engine -> {
            assertNotNull(engine.getKey(KeyBoardKey.A));
            assertNotNull(engine.getKey(MouseKey.LEFT));
            assertNotNull(engine.getMousePos());
            assertNotNull(engine.getWindowMousePos());
            assertDoesNotThrow(engine::getMouseWheel);
            assertDoesNotThrow(engine::getMouseX);
            assertDoesNotThrow(engine::getMouseY);
            assertNotNull(engine.getKeyMap());

            engine.setScreenSize(32, 24);
            assertEquals(32, engine.screenWidth());
            assertEquals(24, engine.screenHeight());
            assertEquals(Vector2D.ofInt(32, 24), engine.getScreenSize());
            assertNotNull(engine.getScreenPixelSize());
            assertNotNull(engine.getPixelSize());
            assertTrue(engine.getDrawTargetWidth() >= 0);
            assertTrue(engine.getDrawTargetHeight() >= 0);
            assertDoesNotThrow(engine::getDrawTarget);
            assertTrue(engine.getElapsedTime() >= 0.0f);
            assertNotNull(engine.getDroppedFiles());
            assertNotNull(engine.getDroppedFilesPoint());
            assertNotNull(engine.getWindowPos());
            assertNotNull(engine.getWindowSize());
            assertTrue(engine.getFramerate() >= 0);
            assertNotNull(engine.getKeyPressCache());
            assertNotNull(engine.convertKeycode(0));
            assertNotNull(engine.getKeySymbol(KeyBoardKey.A));
            assertNotNull(engine.getKeySymbol(KeyBoardKey.A, true, false, false));
            assertEquals(PixelgameEngineReturnCode.OK, engine.showWindowFrame(true));
        });
    }

    @Test
    void softwareDrawingWrappersResolve() {
        withEngine(engine -> {
            Sprite sprite = new Sprite(8, 8);
            sprite.setPixel(0, 0, Pixel.WHITE);
            Vector2D<Float>[] trianglePoints = trianglePoints();
            Vector2D<Float>[] triangleUv = triangleUv();
            Pixel[] triangleColors = triangleColors();
            int layer = engine.createLayer();
            engine.setDrawTarget(layer);

            assertDoesNotThrow(() -> engine.draw(1, 1, Pixel.RED));
            assertDoesNotThrow(() -> engine.draw(Vector2D.ofInt(2, 2), Pixel.GREEN));
            engine.drawLine(0, 0, 10, 10, Pixel.BLUE, 0xFFFFFFFF);
            engine.drawLine(Vector2D.ofInt(0, 1), Vector2D.ofInt(10, 11), Pixel.BLUE);
            engine.drawLine(Vector2D.ofInt(0, 2), Vector2D.ofInt(10, 12), Pixel.BLUE, 0xAAAAAAAA);
            engine.drawRect(1, 1, 5, 5, Pixel.YELLOW);
            engine.drawRect(Vector2D.ofInt(2, 2), Vector2D.ofInt(4, 4), Pixel.YELLOW);
            engine.fillRect(1, 1, 5, 5, Pixel.CYAN);
            engine.fillRect(Vector2D.ofInt(2, 2), Vector2D.ofInt(4, 4), Pixel.CYAN);
            engine.drawTriangle(1, 1, 8, 1, 1, 8, Pixel.MAGENTA);
            engine.drawTriangle(Vector2D.ofInt(2, 2), Vector2D.ofInt(9, 2), Vector2D.ofInt(2, 9), Pixel.MAGENTA);
            engine.fillTriangle(1, 1, 8, 1, 1, 8, Pixel.ORANGE);
            engine.fillTriangle(Vector2D.ofInt(2, 2), Vector2D.ofInt(9, 2), Vector2D.ofInt(2, 9), Pixel.ORANGE);
            engine.fillTexturedTriangle(trianglePoints, triangleUv, triangleColors, sprite);
            engine.fillTexturedPolygon(quadPoints(), quadUv(), quadColors(), sprite, DecalStructure.FAN);
            engine.drawCircle(10, 10, 5, Pixel.WHITE);
            engine.drawCircle(Vector2D.ofInt(11, 11), 5, Pixel.WHITE);
            engine.drawCircle(12, 12, 5, Pixel.WHITE, 0xFF);
            engine.drawCircle(Vector2D.ofInt(13, 13), 5, Pixel.WHITE, 0x0F);
            engine.fillCircle(14, 14, 4, Pixel.WHITE);
            engine.fillCircle(Vector2D.ofInt(15, 15), 4, Pixel.WHITE);
            engine.clear(Pixel.BLACK);
            engine.clearBuffer(Pixel.BLACK);
            engine.clearBuffer(Pixel.BLACK, false);
            LineClipResult clipped = engine.clipLineToDrawTarget(Vector2D.ofInt(-10, 1), Vector2D.ofInt(10, 1));
            assertNotNull(clipped.getStart());
            assertNotNull(clipped.getEnd());
            engine.enablePixelTransfer(true);
        });
    }

    @Test
    void textConsoleAndTextEntryWrappersResolve() {
        withEngine(engine -> {
            engine.consoleWrite("native wrapper test");
            engine.consoleWriteln(" line");
            engine.consoleClear();
            engine.consoleCaptureStdOut(false);
            engine.consoleShow(KeyBoardKey.F1, false);
            assertTrue(engine.isConsoleShowing());

            engine.textEntryEnable(true, "seed");
            assertTrue(engine.isTextEntryEnabled());
            assertEquals("seed", engine.textEntryGetString());
            assertTrue(engine.textEntryGetCursor() >= 0);
            engine.textEntryEnable(false);
            assertFalse(engine.isTextEntryEnabled());
        });
    }

    @Test
    void spriteDrawTargetAndPixelModeWrappersResolve() {
        withEngine(engine -> {
            Sprite sprite = new Sprite(8, 8);
            SpritePatch patch = sprite.patch(Vector2D.ofInt(0, 0), Vector2D.ofInt(4, 4));
            AtomicBoolean customPixelModeCalled = new AtomicBoolean(false);

            engine.drawSprite(1, 1, sprite, 1, FlipMode.NONE);
            engine.drawSprite(Vector2D.ofInt(2, 2), sprite, 1, FlipMode.HORIZONTAL);
            engine.drawSprite(Vector2D.ofFloat(1.0f, 1.0f), patch);
            engine.drawSprite(Vector2D.ofFloat(2.0f, 2.0f), patch, Vector2D.ofFloat(1.0f, 1.0f));
            engine.drawPartialSprite(1, 1, sprite, 0, 0, 4, 4, 1);
            engine.drawPartialSprite(1, 1, sprite, 0, 0, 4, 4, 1, FlipMode.VERTICAL);
            engine.drawPartialSprite(Vector2D.ofInt(1, 1), sprite, Vector2D.ofInt(0, 0), Vector2D.ofInt(4, 4), 1, FlipMode.BOTH);

            engine.setDrawTarget(sprite);
            assertEquals(8, engine.getDrawTargetWidth());
            assertEquals(8, engine.getDrawTargetHeight());
            engine.setDrawTarget((Sprite) null);

            engine.setPixelMode(PixelMode.NORMAL);
            assertEquals(PixelMode.NORMAL, engine.getPixelMode());
            engine.setPixelMode(PixelMode.ALPHA);
            assertEquals(PixelMode.ALPHA, engine.getPixelMode());
            engine.setPixelBlend(0.5f);
            engine.setPixelMode((x, y, sourcePixel, targetPixel) -> {
                customPixelModeCalled.set(true);
                return sourcePixel;
            });
            engine.draw(3, 3, Pixel.WHITE);
            assertTrue(customPixelModeCalled.get());
            engine.setPixelMode(PixelMode.NORMAL);
        });
    }

    @Test
    void decalDrawingWrappersResolve() {
        withEngine(engine -> {
            Sprite sprite = new Sprite(16, 16);
            Decal decal = new Decal(sprite);
            Vector2D<Float>[] quad = quadPoints();
            Vector2D<Float>[] uv = quadUv();
            Pixel[] colors = quadColors();
            DecalPatch patch = decal.patch(Vector2D.ofInt(0, 0), Vector2D.ofInt(8, 8));

            int layer = engine.createLayer();
            engine.setDrawTarget(layer);
            engine.setDecalMode(DecalMode.NORMAL);
            engine.setDecalStructure(DecalStructure.FAN);
            engine.drawDecal(Vector2D.ofFloat(1.0f, 1.0f), decal);
            engine.drawDecal(Vector2D.ofFloat(2.0f, 2.0f), decal, Vector2D.ofFloat(1.0f, 1.0f), Pixel.WHITE);
            engine.drawPartialDecal(Vector2D.ofFloat(1.0f, 1.0f), decal, Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(8.0f, 8.0f));
            engine.drawPartialDecal(Vector2D.ofFloat(1.0f, 1.0f), decal, Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(8.0f, 8.0f), Vector2D.ofFloat(1.0f, 1.0f), Pixel.WHITE);
            engine.drawPartialDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 8.0f), decal, Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.WHITE);
            engine.drawDecal(Vector2D.ofFloat(3.0f, 3.0f), patch);
            engine.drawDecal(Vector2D.ofFloat(4.0f, 4.0f), patch, Vector2D.ofFloat(1.0f, 1.0f));
            engine.drawExplicitDecal(decal, quad, uv, colors);
            engine.drawWarpedDecal(decal, quad, Pixel.WHITE);
            engine.drawPartialWarpedDecal(decal, quad, Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.WHITE);
            engine.drawRotatedDecal(Vector2D.ofFloat(1.0f, 1.0f), decal, 0.1f, Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(1.0f, 1.0f), Pixel.WHITE);
            engine.drawPartialRotatedDecal(Vector2D.ofFloat(1.0f, 1.0f), decal, 0.1f, Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(0.0f, 0.0f), Vector2D.ofFloat(8.0f, 8.0f), Vector2D.ofFloat(1.0f, 1.0f), Pixel.WHITE);
            engine.drawRectDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.WHITE);
            engine.fillRectDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.WHITE);
            engine.gradientFillRectDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.RED, Pixel.GREEN, Pixel.BLUE, Pixel.WHITE);
            engine.gradientLineDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.RED, Pixel.BLUE, 1);
            engine.fillTriangleDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 1.0f), Vector2D.ofFloat(1.0f, 8.0f), Pixel.WHITE);
            engine.gradientTriangleDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 1.0f), Vector2D.ofFloat(1.0f, 8.0f), Pixel.RED, Pixel.GREEN, Pixel.BLUE);
            engine.drawPolygonDecal(decal, quad, uv, Pixel.WHITE);
            engine.drawPolygonDecal(decal, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, uv, Pixel.WHITE);
            engine.drawPolygonDecal(decal, quad, uv, colors);
            engine.drawPolygonDecal(decal, quad, uv, colors, Pixel.WHITE);
            engine.drawPolygonDecal(decal, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, uv, colors, Pixel.WHITE);
            engine.drawLineDecal(Vector2D.ofFloat(1.0f, 1.0f), Vector2D.ofFloat(8.0f, 8.0f), Pixel.WHITE);
        });
    }

    //    @Disabled("Text/font drawing depends on engine font resources initialized during Start(); direct Construct-only tests can crash native code.")
    @Test
    void textAndFontRenderingWrappersRequireStartedEngine() {
        withStartedEngine(engine -> {
            engine.drawString(1, 1, "abc", Pixel.WHITE);
            engine.drawString(1, 10, "abc", Pixel.WHITE, 2);
            engine.drawString(Vector2D.ofInt(1, 20), "abc", Pixel.WHITE);
            engine.drawString(Vector2D.ofInt(1, 30), "abc", Pixel.WHITE, 2);
            engine.drawStringProp(1, 1, "abc", Pixel.WHITE);
            engine.drawStringProp(1, 10, "abc", Pixel.WHITE, 2);
            engine.drawStringProp(Vector2D.ofInt(1, 20), "abc", Pixel.WHITE);
            engine.drawStringProp(Vector2D.ofInt(1, 30), "abc", Pixel.WHITE, 2);
            assertTrue(engine.getTextSize("abc").getX() > 0);
            assertTrue(engine.getTextSizeProp("abc").getX() > 0);
            assertNotNull(engine.getFontSprite());

            Sprite sprite = new Sprite(16, 16);
            Decal decal = new Decal(sprite);
            engine.drawStringDecal(Vector2D.ofFloat(1.0f, 1.0f), "abc", Pixel.WHITE, Vector2D.ofFloat(1.0f, 1.0f));
            engine.drawStringPropDecal(Vector2D.ofFloat(1.0f, 10.0f), "abc", Pixel.WHITE, Vector2D.ofFloat(1.0f, 1.0f));
            engine.drawRotatedStringDecal(Vector2D.ofFloat(1.0f, 1.0f), "abc", 0.1f, Vector2D.ofFloat(0.0f, 0.0f), Pixel.WHITE, Vector2D.ofFloat(1.0f, 1.0f));
            engine.drawRotatedStringPropDecal(Vector2D.ofFloat(1.0f, 1.0f), "abc", 0.1f, Vector2D.ofFloat(0.0f, 0.0f), Pixel.WHITE, Vector2D.ofFloat(1.0f, 1.0f));
        });
    }

    @Test
    void batchedEngineRunsQueuedDrawCallsAtEndOfFrame() {
        withStartedBatchedEngine(engine -> assertTrue(engine.didBatchFrame()));
    }


    @Test
    void layerAdvancedAndHw3dWrappersResolve() {
        withEngine(engine -> {
            Sprite sprite = new Sprite(8, 8);
            Decal decal = new Decal(sprite);
            int layer = engine.createLayer();

            assertTrue(engine.getLayerCount() > layer);
            engine.enableLayer(layer, true);
            assertTrue(engine.isLayerEnabled(layer));
            engine.setLayerOffset(layer, Vector2D.ofFloat(1.0f, 2.0f));
            engine.setLayerOffset(layer, 3.0f, 4.0f);
            assertNotNull(engine.getLayerOffset(layer));
            engine.setLayerScale(layer, 1.0f, 1.0f);
            engine.setLayerScale(layer, Vector2D.ofFloat(1.0f, 1.0f));
            assertNotNull(engine.getLayerScale(layer));
            engine.setLayerTint(layer, Pixel.WHITE);
            assertEquals(Pixel.WHITE, engine.getLayerTint(layer));
            engine.setLayerCustomRenderFunction(layer, () -> {
            });
            engine.setDrawTarget(layer);
            engine.setDrawTarget(layer, true);

            engine.advManualRenderEnable(false);
            engine.advHardwareClip(false, Vector2D.ofInt(0, 0), Vector2D.ofInt(32, 32));
            engine.advHardwareClip(false, Vector2D.ofInt(0, 0), Vector2D.ofInt(32, 32), false);
            engine.advFlushLayer(layer);
            engine.advFlushLayerDecals(layer);
            engine.advFlushLayerGpuTasks(layer);

            float[] identity = identityMatrix();
            engine.hw3dProjection(identity);
            engine.hw3dEnableDepthTest(true);
            engine.hw3dSetCullMode(CullMode.NONE);
            engine.hw3dDrawObject(identity, decal, DecalStructure.LIST,
                    new float[]{0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1},
                    new float[]{0, 0, 1, 0, 0, 1},
                    new Pixel[]{Pixel.WHITE, Pixel.WHITE, Pixel.WHITE},
                    Pixel.WHITE);
            engine.hw3dDrawLine(identity, new float[]{0, 0, 0, 1}, new float[]{1, 1, 0, 1}, Pixel.WHITE);
            engine.hw3dDrawLineBox(identity, new float[]{0, 0, 0, 1}, new float[]{1, 1, 1, 0}, Pixel.WHITE);
        });
    }

    @Disabled("start() opens the real engine loop and blocks until the window closes; run manually when testing window creation.")
    @Test
    void startOpensWindowAndRunsLoop() {
        withStartedEngine(PixelGameEngine::start);
    }

    @FunctionalInterface
    private interface EngineAction {
        void run(TestEngine engine);
    }

    @FunctionalInterface
    private interface BatchedEngineAction {
        void run(BatchedTestEngine engine);
    }

    public static final class TestEngine extends PixelGameEngine {
        private final CountDownLatch started = new CountDownLatch(1);
        private volatile boolean run = true;

        public TestEngine() {
            super(64, 64, 1, 1);
        }

        @Override
        public boolean onUserCreate() {
            markStarted();
            return true;
        }

        @Override
        public boolean onUserUpdate(float delta) {
            return run;
        }

        public void stop() {
            run = false;
        }

        private void awaitStarted() {
            try {
                if (!started.await(5, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Timed out waiting for test engine to start");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for test engine to start", e);
            }
        }

        private void markStarted() {
            started.countDown();
        }
    }

    public static final class BatchedTestEngine extends BatchedPixelGameEngine {
        private final CountDownLatch started = new CountDownLatch(1);
        private final CountDownLatch firstUpdate = new CountDownLatch(1);
        private volatile boolean run = true;
        private final AtomicBoolean batchedFrameSeen = new AtomicBoolean();

        public BatchedTestEngine() {
            super(64, 64, 1, 1);
        }

        @Override
        public boolean onUserCreate() {
            markStarted();
            return true;
        }

        @Override
        public boolean onUserUpdate(float delta) {
            if (batchedFrameSeen.compareAndSet(false, true)) {
                draw(1, 1, Pixel.RED);
                drawLine(0, 0, 4, 4, Pixel.GREEN, 0xFFFFFFFF);
                clear(Pixel.BLACK);
                setPixelMode(PixelMode.ALPHA);
                setPixelBlend(0.5f);
            }
            firstUpdate.countDown();
            return run;
        }

        public void stop() {
            run = false;
        }

        public boolean didBatchFrame() {
            return batchedFrameSeen.get();
        }

        private void awaitStarted() {
            try {
                if (!started.await(5, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Timed out waiting for batched test engine to start");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for batched test engine to start", e);
            }
        }

        private void awaitFirstUpdate() {
            try {
                if (!firstUpdate.await(5, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Timed out waiting for batched test engine to update");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for batched test engine to update", e);
            }
        }

        private void markStarted() {
            started.countDown();
        }
    }
}
