package org.kurodev.jpixelgameengine.impl.ui.base;

import lombok.Getter;
import lombok.Setter;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.impl.ui.UIManager;
import org.kurodev.jpixelgameengine.pos.Vector2D;

@Getter
@Setter
public abstract class IntersectComponent extends UiComponent {
    private int x;
    private int y;
    private int width;
    private int height;

    protected IntersectComponent(int x, int y, int width, int height) {
        this(x, y, width, height, 0);
    }

    protected IntersectComponent(int x, int y, int width, int height, int priority) {
        super(priority);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public final void draw(PixelGameEngine engine) {
        Vector2D<Integer> mouse = engine.getMousePos();
        draw(engine, checkBounding(mouse.getX(), mouse.getY()));
    }

    @Override
    public final void listen(UIManager manager) {
        Vector2D<Integer> mouse = manager.getMousePos();
        listen(manager, checkBounding(mouse.getX(), mouse.getY()));
    }

    private boolean checkBounding(int x, int y) {
        return x >= this.x && y >= this.y && x < this.x + width && y < this.y + height;
    }

    protected abstract void draw(PixelGameEngine engine, boolean intersecting);

    protected abstract void listen(UIManager manager, boolean intersecting);
}
