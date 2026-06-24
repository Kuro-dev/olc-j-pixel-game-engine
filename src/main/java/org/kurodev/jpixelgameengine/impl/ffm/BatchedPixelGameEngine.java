package org.kurodev.jpixelgameengine.impl.ffm;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.PixelMode;
import org.kurodev.jpixelgameengine.gfx.PixelModeFunction;
import org.kurodev.jpixelgameengine.gfx.decal.DecalMode;
import org.kurodev.jpixelgameengine.gfx.decal.DecalStructure;
import org.kurodev.jpixelgameengine.gfx.sprite.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

import static org.kurodev.jpixelgameengine.impl.ffm.NativeFunction.LINKER;

/**
 * Variant of {@link PixelGameEngine} that batches direct screen draw calls and draw-state changes
 * until the end of each update cycle.
 * <p>
 * Methods overridden in this class add a compact command to an internal queue. The queue is flushed
 * after {@link #onUserUpdate(float)} returns, so batched calls keep their order within a frame.
 */
public abstract class BatchedPixelGameEngine extends PixelGameEngine {
    private static final int DRAW_COMMAND_CAPACITY = 12;
    private static final long DRAW_COMMAND_SIZE_BYTES = 64L;
    private static final long DRAW_COMMAND_ARGS_OFFSET = 4L;
    private static final long DRAW_COMMAND_TEXT_OFFSET = 56L;
    private static final int CMD_DRAW = 1;
    private static final int CMD_DRAW_LINE = 2;
    private static final int CMD_DRAW_RECT = 3;
    private static final int CMD_FILL_RECT = 4;
    private static final int CMD_DRAW_TRIANGLE = 5;
    private static final int CMD_FILL_TRIANGLE = 6;
    private static final int CMD_DRAW_CIRCLE = 7;
    private static final int CMD_FILL_CIRCLE = 8;
    private static final int CMD_CLEAR = 9;
    private static final int CMD_CLEAR_BUFFER = 10;
    private static final int CMD_DRAW_STRING = 11;
    private static final int CMD_DRAW_STRING_PROP = 12;
    private static final int CMD_SET_SCREEN_SIZE = 20;
    private static final int CMD_SET_DRAW_TARGET_SPRITE = 21;
    private static final int CMD_SET_DRAW_TARGET_LAYER = 22;
    private static final int CMD_SET_PIXEL_MODE = 23;
    private static final int CMD_SET_PIXEL_MODE_CUSTOM = 24;
    private static final int CMD_SET_PIXEL_BLEND = 25;
    private static final int CMD_ENABLE_PIXEL_TRANSFER = 26;
    private static final int CMD_SET_DECAL_MODE = 27;
    private static final int CMD_SET_DECAL_STRUCTURE = 28;
    private static final Logger log = LoggerFactory.getLogger(BatchedPixelGameEngine.class);

    private final List<QueuedDrawCommand> drawQueue = new ArrayList<>(64);
    private MemorySegment drawCommandBuffer = MemorySegment.NULL;
    private int drawCommandBufferCapacity;
    private PixelModeFunction customPixelModeFunction;
    private MemorySegment customPixelModeStub = MemorySegment.NULL;

    /**
     * Creates a batched engine using the default window flags.
     */
    public BatchedPixelGameEngine(int width, int height, int pixelWidth, int pixelHeight) {
        super(width, height, pixelWidth, pixelHeight);
        log.info("Creating BatchedPixelGameEngine with width: {}, height: {}", width, height);
    }

    /**
     * Creates a batched engine with explicit native window flags.
     */
    public BatchedPixelGameEngine(int width, int height, int pixelWidth, int pixelHeight, boolean fullScreen, boolean vSync, boolean cohesion, boolean realWindow) {
        super(width, height, pixelWidth, pixelHeight, fullScreen, vSync, cohesion, realWindow);
        log.info("Creating BatchedPixelGameEngine with width: {}, height: {}", width, height);
    }

    /**
     * Runs the user update callback and flushes all queued batched commands before the frame ends.
     *
     * @param delta time since the last frame
     * @return {@code true} while the engine should keep running
     */
    @Override
    protected boolean internalOnUserUpdate(float delta) {
        boolean keepRunning = onUserUpdate(delta);
        flushQueuedDrawCommands();
        return keepRunning;
    }

    /**
     * Queues a single-pixel draw command for the end-of-frame batch.
     *
     * @return always {@code true} because the native result is not available until the queue is flushed
     */
    @Override
    public boolean draw(int x, int y, Pixel p) {
        enqueueDrawCommand(CMD_DRAW, null, MemorySegment.NULL, x, y, p.getRGBA());
        return true;
    }

    /**
     * Queues a line draw command for the end-of-frame batch.
     */
    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Pixel p, int pattern) {
        enqueueDrawCommand(CMD_DRAW_LINE, null, MemorySegment.NULL, x1, y1, x2, y2, p.getRGBA(), pattern);
    }

    /**
     * Queues an outlined rectangle draw command for the end-of-frame batch.
     */
    @Override
    public void drawRect(int x, int y, int width, int height, Pixel p) {
        enqueueDrawCommand(CMD_DRAW_RECT, null, MemorySegment.NULL, x, y, width, height, p.getRGBA());
    }

    /**
     * Queues a filled rectangle draw command for the end-of-frame batch.
     * <p>
     * This override also forwards the call immediately to the native engine, matching the current
     * implementation behavior.
     */
    @Override
    public void fillRect(int x, int y, int width, int height, Pixel p) {
        enqueueDrawCommand(CMD_FILL_RECT, null, MemorySegment.NULL, x, y, width, height, p.getRGBA());
        methods.fillRect.invoke(instancePtr, x, y, width, height, p.getRGBA());
    }

    /**
     * Queues an outlined triangle draw command for the end-of-frame batch.
     * <p>
     * This override also forwards the call immediately to the native engine, matching the current
     * implementation behavior.
     */
    @Override
    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p) {
        enqueueDrawCommand(CMD_DRAW_TRIANGLE, null, MemorySegment.NULL, x1, y1, x2, y2, x3, y3, p.getRGBA());
        methods.drawTriangle.invoke(instancePtr, x1, y1, x2, y2, x3, y3, p.getRGBA());
    }

    /**
     * Queues a filled triangle draw command for the end-of-frame batch.
     * <p>
     * This override also forwards the call immediately to the native engine, matching the current
     * implementation behavior.
     */
    @Override
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Pixel p) {
        enqueueDrawCommand(CMD_FILL_TRIANGLE, null, MemorySegment.NULL, x1, y1, x2, y2, x3, y3, p.getRGBA());
        methods.fillTriangle.invoke(instancePtr, x1, y1, x2, y2, x3, y3, p.getRGBA());
    }

    /**
     * Queues a screen-size change; it takes effect when the batch is flushed.
     */
    @Override
    public void setScreenSize(int width, int height) {
        enqueueDrawCommand(CMD_SET_SCREEN_SIZE, null, MemorySegment.NULL, width, height);
    }

    /**
     * Queues a draw-target change to a sprite; it takes effect when the batch is flushed.
     */
    @Override
    public void setDrawTarget(Sprite sprite) {
        enqueueDrawCommand(CMD_SET_DRAW_TARGET_SPRITE, null, sprite == null ? MemorySegment.NULL : sprite.getSpritePtr());
    }

    /**
     * Queues a draw-target change to a layer; it takes effect when the batch is flushed.
     */
    @Override
    public void setDrawTarget(int layer, boolean dirty) {
        enqueueDrawCommand(CMD_SET_DRAW_TARGET_LAYER, null, MemorySegment.NULL, layer, dirty ? 1 : 0);
    }

    /**
     * Queues fixed-width bitmap text for the end-of-frame batch.
     */
    @Override
    public void drawString(int x, int y, String text, Pixel color, int scale) {
        enqueueDrawCommand(CMD_DRAW_STRING, text, MemorySegment.NULL, x, y, color.getRGBA(), scale);
    }

    /**
     * Queues proportional bitmap text for the end-of-frame batch.
     */
    @Override
    public void drawStringProp(int x, int y, String text, Pixel color, int scale) {
        enqueueDrawCommand(CMD_DRAW_STRING_PROP, text, MemorySegment.NULL, x, y, color.getRGBA(), scale);
    }

    /**
     * Queues a full outlined circle draw command for the end-of-frame batch.
     */
    @Override
    public void drawCircle(int x, int y, int radius, Pixel color) {
        enqueueDrawCommand(CMD_DRAW_CIRCLE, null, MemorySegment.NULL, x, y, radius, color.getRGBA(), 0xFF);
    }

    /**
     * Queues a masked outlined circle draw command for the end-of-frame batch.
     */
    @Override
    public void drawCircle(int x, int y, int radius, Pixel color, int mask) {
        enqueueDrawCommand(CMD_DRAW_CIRCLE, null, MemorySegment.NULL, x, y, radius, color.getRGBA(), mask);
    }

    /**
     * Queues a filled circle draw command for the end-of-frame batch.
     */
    @Override
    public void fillCircle(int x, int y, int radius, Pixel color) {
        enqueueDrawCommand(CMD_FILL_CIRCLE, null, MemorySegment.NULL, x, y, radius, color.getRGBA());
    }

    /**
     * Queues a pixel-mode change; following queued draw commands observe this order when flushed.
     */
    @Override
    public void setPixelMode(PixelMode mode) {
        enqueueDrawCommand(CMD_SET_PIXEL_MODE, null, MemorySegment.NULL, mode.ordinal());
    }

    /**
     * Queues a custom pixel-mode function for batched software drawing.
     */
    @Override
    public void setPixelMode(PixelModeFunction pixelModeFunction) {
        try {
            this.customPixelModeFunction = pixelModeFunction;
            MethodHandle handle = MethodHandles.lookup()
                    .findVirtual(BatchedPixelGameEngine.class, "invokeCustomPixelMode",
                            MethodType.methodType(int.class, int.class, int.class, int.class, int.class))
                    .bindTo(this);
            customPixelModeStub = LINKER.upcallStub(handle,
                    FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT),
                    arena);

            enqueueDrawCommand(CMD_SET_PIXEL_MODE_CUSTOM, null, customPixelModeStub);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Queues a blend-factor change for batched alpha drawing.
     */
    @Override
    public void setPixelBlend(float blend) {
        enqueueDrawCommand(CMD_SET_PIXEL_BLEND, null, MemorySegment.NULL, Float.floatToRawIntBits(blend));
    }

    /**
     * Queues a frame-buffer clear for the end-of-frame batch.
     */
    @Override
    public void clear(Pixel color) {
        enqueueDrawCommand(CMD_CLEAR, null, MemorySegment.NULL, color.getRGBA());
    }

    /**
     * Queues a color/depth buffer clear for the end-of-frame batch.
     */
    @Override
    public void clearBuffer(Pixel color, boolean depth) {
        enqueueDrawCommand(CMD_CLEAR_BUFFER, null, MemorySegment.NULL, color.getRGBA(), depth ? 1 : 0);
    }

    /**
     * Queues pixel-transfer enablement for the end-of-frame batch.
     */
    @Override
    public void enablePixelTransfer(boolean enable) {
        enqueueDrawCommand(CMD_ENABLE_PIXEL_TRANSFER, null, MemorySegment.NULL, enable ? 1 : 0);
    }

    /**
     * Queues a decal-mode change for subsequent queued decal-related work.
     */
    @Override
    public void setDecalMode(DecalMode mode) {
        enqueueDrawCommand(CMD_SET_DECAL_MODE, null, MemorySegment.NULL, mode.ordinal());
    }

    /**
     * Queues a decal-structure change for subsequent queued decal-related work.
     */
    @Override
    public void setDecalStructure(DecalStructure structure) {
        enqueueDrawCommand(CMD_SET_DECAL_STRUCTURE, null, MemorySegment.NULL, structure.ordinal());
    }

    private void enqueueDrawCommand(int opcode, String text, MemorySegment pointer, int... args) {
        drawQueue.add(new QueuedDrawCommand(opcode, text, pointer, args));
    }

    private void flushQueuedDrawCommands() {
        if (drawQueue.isEmpty()) {
            return;
        }

        try (Arena local = Arena.ofConfined()) {
            int commandCount = drawQueue.size();
            MemorySegment commandBuffer = ensureDrawCommandBuffer(commandCount);
            for (int i = 0; i < commandCount; i++) {
                QueuedDrawCommand command = drawQueue.get(i);
                MemorySegment commandSegment = commandBuffer.asSlice((long) i * DRAW_COMMAND_SIZE_BYTES, DRAW_COMMAND_SIZE_BYTES);
                commandSegment.set(ValueLayout.JAVA_INT, 0, command.opcode);

                int argCount = Math.min(command.args.length, DRAW_COMMAND_CAPACITY);
                for (int j = 0; j < argCount; j++) {
                    commandSegment.set(ValueLayout.JAVA_INT, DRAW_COMMAND_ARGS_OFFSET + (long) j * Integer.BYTES, command.args[j]);
                }
                for (int j = argCount; j < DRAW_COMMAND_CAPACITY; j++) {
                    commandSegment.set(ValueLayout.JAVA_INT, DRAW_COMMAND_ARGS_OFFSET + (long) j * Integer.BYTES, 0);
                }

                MemorySegment textPtr = command.text == null ? MemorySegment.NULL : local.allocateFrom(command.text);
                if (command.pointer != null && !MemorySegment.NULL.equals(command.pointer)) {
                    textPtr = command.pointer;
                }
                commandSegment.set(ValueLayout.ADDRESS, DRAW_COMMAND_TEXT_OFFSET, textPtr);
            }
            methods.flushDrawQueue.invoke(instancePtr, commandBuffer, commandCount);
            drawQueue.clear();
        }
    }

    private MemorySegment ensureDrawCommandBuffer(int commandCount) {
        if (!MemorySegment.NULL.equals(drawCommandBuffer) && drawCommandBufferCapacity >= commandCount) {
            return drawCommandBuffer;
        }
        drawCommandBufferCapacity = Math.max(commandCount, Math.max(16, drawCommandBufferCapacity * 2));
        drawCommandBuffer = arena.allocate(DRAW_COMMAND_SIZE_BYTES * drawCommandBufferCapacity, 8);
        return drawCommandBuffer;
    }

    @SuppressWarnings("unused")
    private int invokeCustomPixelMode(int x, int y, int sourceRgba, int targetRgba) {
        Pixel result = customPixelModeFunction.apply(x, y, new Pixel(sourceRgba), new Pixel(targetRgba));
        return result.getRGBA();
    }

    private record QueuedDrawCommand(
            int opcode,
            String text,
            MemorySegment pointer,
            int[] args
    ) {
        private QueuedDrawCommand {
            args = args == null ? new int[0] : args.clone();
        }
    }
}
