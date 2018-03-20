package com.isen.khass.metronometutorial;

/**
 * Created by khass on 16/03/2018.
 */

public enum Beats {
    un("1"),
    deux("2"),
    trois("3"),
    quatre("4"),
    cinq("5"),
    six("6"),
    sept("7"),
    huit("8"),
    neuf("9"),
    dix("10"),
    onze("11"),
    douze("12"),
    treize("13"),
    quatorze("14"),
    quinze("15"),
    seize("16");

    private String beat;

    Beats(String beat) {
        this.beat = beat;
    }

    @Override public String toString() {
        return beat;
    }

    public short getNum() {
        return Short.parseShort(beat);
    }
}
