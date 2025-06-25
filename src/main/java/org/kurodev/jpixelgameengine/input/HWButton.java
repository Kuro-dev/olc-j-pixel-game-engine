package org.kurodev.jpixelgameengine.input;

import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;

import java.util.Objects;

@NativeCallCandidate
public class HWButton {
    private final boolean pressed;
    private final boolean released;
    private final boolean held;

    public HWButton(boolean pressed, boolean released, boolean held) {
        this.pressed = pressed;
        this.released = released;
        this.held = held;
    }

    @Override
    public String toString() {
        return "HWButton{" +
                "pressed=" + pressed +
                ", released=" + released +
                ", held=" + held +
                '}';
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isReleased() {
        return released;
    }

    public boolean isHeld() {
        return held;
    }

    @Override

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HWButton hwButton = (HWButton) o;
        return pressed == hwButton.pressed && released == hwButton.released && held == hwButton.held;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pressed, released, held);
    }

}
