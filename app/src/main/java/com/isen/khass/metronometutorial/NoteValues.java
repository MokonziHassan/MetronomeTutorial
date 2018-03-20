package com.isen.khass.metronometutorial;

/**
 * Created by khass on 16/03/2018.
 */

public enum  NoteValues {

    un("1"),
    quatre("4"),
    huit("8"),
    seize("16"),
    trentedeux("32");

    private String noteValue;

    NoteValues(String notevalue){
        this.noteValue = notevalue;
    }
    @Override
    public String toString(){
        return noteValue;
    }
}
