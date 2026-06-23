package org.kurodev.jpixelgameengine.gfx;

import org.kurodev.jpixelgameengine.pos.Vector2D;

/**
 * Result returned by clipping a line against the active draw target.
 */
public final class LineClipResult {
    private final boolean visible;
    private final Vector2D<Integer> start;
    private final Vector2D<Integer> end;

    public LineClipResult(boolean visible, Vector2D<Integer> start, Vector2D<Integer> end) {
        this.visible = visible;
        this.start = start;
        this.end = end;
    }

    public boolean isVisible() {
        return visible;
    }

    public Vector2D<Integer> getStart() {
        return start;
    }

    public Vector2D<Integer> getEnd() {
        return end;
    }
}
