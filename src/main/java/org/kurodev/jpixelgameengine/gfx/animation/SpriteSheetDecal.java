package org.kurodev.jpixelgameengine.gfx.animation;

import org.kurodev.jpixelgameengine.gfx.decal.Decal;
import org.kurodev.jpixelgameengine.pos.FloatVector2D;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public class SpriteSheetDecal {
    private final Decal decal;
    private final int width;
    private final int height;
    private final Vector2D<Float> frameSize;

    public SpriteSheetDecal(Decal decal, Vector2D<Float> frameSize) {
        this.decal = decal;
        this.width = decal.getSprite().getWidth();
        this.height = decal.getSprite().getHeight();
        this.frameSize = frameSize;
    }

    private SpriteSheetDecal(Decal decal, int frameSizePx, int frameSizePy) {
        this(decal, Vector2D.ofFloat(frameSizePx, frameSizePy));
    }

    public Animation animateTimeBased(Vector2D<Integer> startFrame, int frameCount, double timePerFrameSeconds, boolean loop) {
        Vector2D<Float>[] frames = new FloatVector2D[frameCount];
        int frameCountX = (int) (width / frameSize.getX());
        for (int i = 0; i < frameCount; i++) {
            float posX = (startFrame.getX() + (frameSize.getX() * i));
            int yOffset = i / frameCountX;
            float posY = startFrame.getY() + (int) (yOffset * frameSize.getY());
            frames[i] = Vector2D.ofFloat(posX % width, posY % height);
        }
        return new Animation(decal, loop, frames, frameSize, timePerFrameSeconds);
    }
}
