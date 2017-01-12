package com.novoda.github.reports.web.hooks.lambda;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class BufferedProxyOutputStream extends OutputStream {

    private int[] buffer;
    private int position;

    private Optional<OutputStream> outputStream;

    public BufferedProxyOutputStream() {
        this(1024);
    }

    public BufferedProxyOutputStream(int capacity) {
        this.buffer = new int[capacity];
        this.outputStream = Optional.empty();
    }

    @Override
    public void write(int b) throws IOException {
        if (outputStream.isPresent()) {
            outputStream.get().write(b);
        } else {
            writeToBuffer(b);
        }
    }

    private void writeToBuffer(int b) throws IOException {
        if (position == buffer.length) {
            throw new IOException("Internal buffer is full. Either attach an OutputStream or increase its capacity.");
        }
        buffer[position++] = b;
    }

    public void attach(OutputStream outputStream) throws IOException {
        this.outputStream = Optional.of(outputStream);
        flushBuffer();
    }

    private void flushBuffer() throws IOException {
        for (int value : buffer) {
            this.outputStream.get().write(value);
        }
        buffer = null;
    }

    @Override
    public void flush() throws IOException {
        if (outputStream.isPresent()) {
            outputStream.get().flush();
        } else {
            throw new IOException("Cannot flush to non-existing stream. You need to attach an OutputStream first.");
        }
    }

    @Override
    public void close() throws IOException {
        if (outputStream.isPresent()) {
            outputStream.get().close();
        }
        buffer = null;
    }
}
