package org.kurodev;

public class Example {


    private final String text;

    public Example(String text) {
        this.text = text;
    }

    public native int doStuff(int someInt);
}
