package org.kurodev.jpixelgameengine.gfx;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.Cleaner;

@Slf4j
public record OlcReferenceCleaner(Cleaner.Cleanable cleanable) implements Runnable {
    @Override
    public void run() {
        cleanable.clean();
    }
}
