package org.kurodev.jpixelgameengine.impl.ui;

import lombok.Getter;
import lombok.Setter;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.impl.ui.base.MouseState;
import org.kurodev.jpixelgameengine.impl.ui.base.UiComponent;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UIManager {
    public final List<UiComponent> components = new ArrayList<>();

    @Getter
    @Setter
    private boolean enabled = false;

    private MouseState mouse = null;

    @Getter
    private final PixelGameEngine engine;

    public UIManager(PixelGameEngine pixelGameEngine) {
        this.engine = pixelGameEngine;
    }

    public void registerComponent(UiComponent component) {
        components.add(component);
        components.sort(Comparator.comparingInt(UiComponent::getPriority).reversed());
    }

    public void removeComponent(UiComponent component) {
        components.remove(component);
    }

    private void clearState() {
        mouse = new MouseState();
    }

    public void draw() {
        clearState();
        components.stream().filter(UiComponent::isVisible).forEach(component -> {
            component.draw(engine);
            if (component.isEnabled()) {
                component.listen(this);
            }
        });
    }


    public Vector2D<Integer> getMousePos() {
        if (mouse.getPos() == null) {
            mouse.setPos(engine.getMousePos());
        }
        return mouse.getPos();
    }

    /**
     * @return State of the left mouse button
     */
    public HWButton getMousePrimary() {
        if (mouse.getPrimary() == null) {
            mouse.setPrimary(engine.getKey(MouseKey.LEFT));
        }
        return mouse.getPrimary();
    }

    /**
     * @return State of the right mouse button
     */
    public HWButton getMouseSecondary() {
        if (mouse.getSecondary() == null) {
            mouse.setSecondary(engine.getKey(MouseKey.RIGHT));
        }
        return mouse.getSecondary();
    }


    /**
     * @return State of the middle mouse button
     */
    public HWButton getMouseTertiary() {
        if (mouse.getTertiary() == null) {
            mouse.setTertiary(engine.getKey(MouseKey.MIDDLE));
        }
        return mouse.getTertiary();
    }

    public HWButton getKey(KeyBoardKey key) {
        return mouse.getPrimary();
    }
}
