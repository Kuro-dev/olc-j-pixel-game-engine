package org.kurodev.jpixelgameengine.gfx.animation;

import org.kurodev.jpixelgameengine.gfx.Pixel;
import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class Animation {
    private final Decal decal;
    private final boolean loop;
    private final Vector2D<Float>[] frames;
    private final Vector2D<Float> frameSize;
    private final double timePerFrame;
    private int currentFrame = 0;
    private double count;

    public Animation(Decal decal, boolean loop, Vector2D<Float>[] frames, Vector2D<Float> frameSize, double timePerFrame) {
        this.decal = decal;
        this.loop = loop;
        this.frames = frames;
        this.frameSize = frameSize;
        this.timePerFrame = timePerFrame;
        count = timePerFrame;
    }

    public void reset() {
        currentFrame = 0;
    }

    public void draw(float delta, PixelGameEngine engine, Vector2D<Float> pos) {
        draw(delta, engine, pos, FloatVector2D.ONE, Pixel.WHITE);
    }

    public void draw(float delta, PixelGameEngine engine, Vector2D<Float> pos, Vector2D<Float> scale, Pixel tint) {
        engine.drawPartialDecal(pos, decal, frames[currentFrame], frameSize, scale, tint);
        count -= delta;
        if (count <= 0) {
            count = timePerFrame;
            currentFrame++;
            if (loop && currentFrame >= frames.length) {
                currentFrame = 0;
            }
        }
    }
}
