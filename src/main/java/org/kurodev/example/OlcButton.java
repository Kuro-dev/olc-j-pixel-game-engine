package org.kurodev.example;

import lombok.Getter;
import lombok.Setter;
import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.impl.ui.UIManager;
import org.kurodev.jpixelgameengine.impl.ui.base.IntersectComponent;

public class OlcButton extends IntersectComponent {
    private final Runnable onClick;
    @Setter
    @Getter
    private Pixel color = Pixel.WHITE;

    /**
     * TODO: Figure out how to compute width and height automatically
     */
    public OlcButton(int posX, int posY, int width, int height, Runnable onClick) {
        super(posX, posY, width, height);
        this.onClick = onClick;
    }

    @Override
    protected void draw(PixelGameEngine engine, boolean intersecting) {
        Pixel color = this.color;
        if (intersecting) {
            color = Pixel.RED;
        }
        engine.drawRect(getX(), getY(), getWidth(), getHeight(), color);
    }

    @Override
    protected void listen(UIManager manager, boolean intersecting) {
        if (!intersecting) {
            return;
        }
        if (manager.getMousePrimary().isPressed()) {
            onClick.run();
        }
    }

}
