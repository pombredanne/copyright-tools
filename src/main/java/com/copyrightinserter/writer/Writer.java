package com.copyrightinserter.writer;
public interface Writer {
    public void writeLine(String line);

    public void writeLine(String format, Object... args);
}