package org.kurodev.jpixelgameengine.impl.ui.base;

import lombok.Getter;
import lombok.Setter;
import org.kurodev.jpixelgameengine.impl.ffm.PixelGameEngine;
import org.kurodev.jpixelgameengine.impl.ui.UIManager;

public abstract class UiComponent {
    private final int priority;
    /**
     * Whether this component should have it's {@link #listen(UIManager)} method called
     */
    @Getter
    @Setter
    private boolean enabled = true;
    /**
     * Whether this component should be drawn to the screen. False will also disable the listener.
     */
    @Getter
    @Setter
    private boolean visible = true;

    protected UiComponent(int priority) {
        this.priority = priority;
    }

    protected UiComponent() {
        this(0);
    }

    public abstract void draw(final PixelGameEngine engine);

    /**
     * Listener function.
     *
     * @param manager UI manager Will buffer queried keys and buttons between frames
     */
    public abstract void listen(final UIManager manager);

    /**
     * Priority of this component. Higher priority will be drawn last.
     */
    public final int getPriority() {
        return priority;
    }


}
