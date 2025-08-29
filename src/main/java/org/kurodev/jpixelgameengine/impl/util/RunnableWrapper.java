package org.kurodev.jpixelgameengine.impl.util;

public record RunnableWrapper(Runnable delegate) implements Runnable {

    @Override
    public void run() {
        delegate.run();
    }

    public static RunnableWrapper of(Runnable runnable) {
        return new RunnableWrapper(runnable);
    }
}
