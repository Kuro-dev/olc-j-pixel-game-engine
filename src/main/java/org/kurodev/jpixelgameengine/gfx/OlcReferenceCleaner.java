package org.kurodev.jpixelgameengine.gfx;

import java.lang.ref.Cleaner;

public record OlcReferenceCleaner(Cleaner.Cleanable cleanable) implements Runnable {
    @Override
    public void run() {
        cleanable.clean();
    }
}
