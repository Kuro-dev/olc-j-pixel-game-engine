package org.kurodev.jpixelgameengine.gfx;

import org.kurodev.jpixelgameengine.impl.ffm.NativeFunction;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.ref.Cleaner;
import java.nio.file.Path;

/**
 * Wrapper for {@code olc::ResourcePack}, a packed virtual file system for assets.
 */
public final class ResourcePack implements AutoCloseable {
    private static final Cleaner CLEANER = Cleaner.create();
    private static final NativeFunction<MemorySegment> CREATE = new NativeFunction<>("resourcePack_create", ValueLayout.ADDRESS);
    private static final NativeFunction<Void> DESTROY = new NativeFunction<>("resourcePack_destroy", FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));
    private static final NativeFunction<Boolean> ADD_FILE = new NativeFunction<>("resourcePack_addFile", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Boolean> LOAD_PACK = new NativeFunction<>("resourcePack_loadPack", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Boolean> SAVE_PACK = new NativeFunction<>("resourcePack_savePack", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Boolean> LOADED = new NativeFunction<>("resourcePack_loaded", ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> FILE_BUFFER_SIZE = new NativeFunction<>("resourcePack_getFileBufferSize", ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final NativeFunction<Integer> FILE_BUFFER = new NativeFunction<>("resourcePack_getFileBuffer", ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);

    private final Arena arena = Arena.ofAuto();
    private final MemorySegment ptr;
    private final Cleaner.Cleanable cleanable;

    public ResourcePack() {
        ptr = CREATE.invokeExact(segment -> segment);
        cleanable = CLEANER.register(this, new OlcReferenceCleaner(() -> DESTROY.invoke(ptr)));
    }

    /**
     * Adds a file to the pack manifest before saving.
     *
     * @param path file to add
     * @return true if the file was accepted
     */
    public boolean addFile(Path path) {
        return ADD_FILE.invoke(ptr, arena.allocateFrom(path.toString()));
    }

    /**
     * Loads an existing resource pack.
     *
     * @param path pack file path
     * @param key  scramble key
     * @return true if the pack loaded
     */
    public boolean loadPack(Path path, String key) {
        return LOAD_PACK.invoke(ptr, arena.allocateFrom(path.toString()), arena.allocateFrom(key));
    }

    /**
     * Saves all added files into a pack.
     *
     * @param path output pack path
     * @param key  scramble key
     * @return true if the pack was written
     */
    public boolean savePack(Path path, String key) {
        return SAVE_PACK.invoke(ptr, arena.allocateFrom(path.toString()), arena.allocateFrom(key));
    }

    /**
     * @return true when a pack file is currently loaded
     */
    public boolean loaded() {
        return LOADED.invoke(ptr);
    }

    /**
     * Reads a packed file into a Java byte array.
     *
     * @param path path as stored in the pack
     * @return file contents, or an empty array if the file is not present
     */
    public byte[] getFileBuffer(String path) {
        MemorySegment cPath = arena.allocateFrom(path);
        int size = FILE_BUFFER_SIZE.invoke(ptr, cPath);
        if (size <= 0) {
            return new byte[0];
        }

        try (Arena local = Arena.ofConfined()) {
            MemorySegment buffer = local.allocate(size);
            int written = FILE_BUFFER.invoke(ptr, cPath, buffer, size);
            byte[] result = new byte[Math.min(size, Math.max(written, 0))];
            for (int i = 0; i < result.length; i++) {
                result[i] = buffer.getAtIndex(ValueLayout.JAVA_BYTE, i);
            }
            return result;
        }
    }

    public MemorySegment getPtr() {
        return ptr;
    }

    @Override
    public void close() {
        cleanable.clean();
    }
}
