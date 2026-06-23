package org.kurodev.jpixelgameengine.gfx;

/**
 * Custom software pixel blend callback used by {@code PixelGameEngine#setPixelMode(PixelModeFunction)}.
 */
@FunctionalInterface
public interface PixelModeFunction {
    /**
     * Blends a source pixel into the destination pixel.
     *
     * @param x           x coordinate being drawn
     * @param y           y coordinate being drawn
     * @param sourcePixel pixel requested by the draw call
     * @param targetPixel pixel currently present in the draw target
     * @return replacement pixel to write
     */
    Pixel apply(int x, int y, Pixel sourcePixel, Pixel targetPixel);
}
