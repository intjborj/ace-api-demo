package com.hisd3.hismk2.filters

import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

class GZipServletOutputStream extends ServletOutputStream {
    private OutputStream stream

    public GZipServletOutputStream(OutputStream output)
            throws IOException {
        super()
        this.stream = output
    }

    @Override
    public void close() throws IOException {
        this.stream.close()
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush()
    }

    @Override
    public void write(byte []b) throws IOException {
        this.stream.write(b)
    }

    @Override
    public void write(byte []b, int off, int len) throws IOException {
        this.stream.write(b, off, len)
    }

    @Override
    public void write(int b) throws IOException {
        this.stream.write(b)
    }

    @Override
    boolean isReady() {
        return false
    }

    @Override
    void setWriteListener(WriteListener listener) {

    }
}
