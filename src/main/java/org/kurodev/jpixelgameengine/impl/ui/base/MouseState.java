package org.kurodev.jpixelgameengine.impl.ui.base;

import lombok.Getter;
import lombok.Setter;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.pos.Vector2D;

@Getter
@Setter
public class MouseState {
    private HWButton primary;
    private HWButton secondary;
    private HWButton tertiary;
    private Vector2D<Integer> pos;
}
