package org.kurodev.jpixelgameengine;

import org.kurodev.jpixelgameengine.draw.Pixel;
import org.kurodev.jpixelgameengine.impl.NativeCallCandidate;
import org.kurodev.jpixelgameengine.impl.PixelGameEngineInitialiser;
import org.kurodev.jpixelgameengine.impl.PixelGameEngineWrapper;
import org.kurodev.jpixelgameengine.input.HWButton;
import org.kurodev.jpixelgameengine.input.KeyBoardKey;
import org.kurodev.jpixelgameengine.input.MouseKey;
import org.kurodev.jpixelgameengine.pos.Vector2D;

public interface PixelGameEngine {

    static PixelGameEngine getInstance(){
      return  PixelGameEngineWrapper.getInstance();
    }

    static PixelGameEngineInitialiser init(){
        return new PixelGameEngineInitialiser();
    }

    @NativeCallCandidate
    boolean onUserCreate();

    @NativeCallCandidate
    boolean onUserUpdate(float delta);

    @NativeCallCandidate
    boolean onUserDestroy();

    boolean isFocussed();

    boolean draw(Vector2D<? extends Number> pos, Pixel p);

    boolean draw(int x, int y, Pixel p);

    HWButton getKey(KeyBoardKey k);

    HWButton getKey(MouseKey k);

    Vector2D<Integer> getMousePos();

    Vector2D<Integer> getWindowMousePos();

    /**
     * @return a value < 0 if scrolling down, a value > 0 if scrolling up, or 0 if not scrolling
     */
    int getMouseWheel();
}
