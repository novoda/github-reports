package com.novoda.github.reports.batch.persistence;

class PersistBuffer {

    private final int size;

    public static PersistBuffer newInstance(int size) {
        return new PersistBuffer(size);
    }

    private PersistBuffer(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
