package org.kurodev.jpixelgameengine.gfx.sprite;

/**
 * Sampling behavior used by {@link Sprite#sample(float, float)} and bilinear sampling.
 */
public enum SpriteSampleMode {
    NORMAL,
    PERIODIC,
    CLAMP
}
