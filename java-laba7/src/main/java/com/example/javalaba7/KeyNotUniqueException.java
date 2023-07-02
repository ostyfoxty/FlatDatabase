package com.example.javalaba7;

public class KeyNotUniqueException extends Exception {
    // class release version:
    private static final long serialVersionUID = 1L;

    public KeyNotUniqueException(int key) {
        super(new String("Key is not unique: " + key));
    }
}

