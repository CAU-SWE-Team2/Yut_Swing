package com.yut.model;

import java.util.Random;

public class Yut {
    private static final Yut instance = new Yut();

    private YutResult current;

    private Yut() {}

    static Yut getYut() { return instance; }

    void rollYutRandomly() {
        YutResult[] results = YutResult.values();
        current = results[new Random().nextInt(results.length)];
    }

    void rollYutSelected(YutResult result) {
        current = result;
    }

    YutResult getCurrent() { return current; }
}
