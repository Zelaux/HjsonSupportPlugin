package com.zelaux.hjson.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;

public class LegalPrintStream extends PrintStream {
    public LegalPrintStream(@NotNull OutputStream out) {
        super(out);
    }

    public LegalPrintStream(@NotNull OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public LegalPrintStream(@NotNull OutputStream out, boolean autoFlush, @NotNull String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public LegalPrintStream(OutputStream out, boolean autoFlush, Charset charset) {
        super(out, autoFlush, charset);
    }

    public LegalPrintStream(@NotNull String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public LegalPrintStream(@NotNull String fileName, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public LegalPrintStream(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public LegalPrintStream(@NotNull File file) throws FileNotFoundException {
        super(file);
    }

    public LegalPrintStream(@NotNull File file, @NotNull String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void println() {
        super.print("\n");
    }

    @Override
    public void println(boolean x) {
        super.print(x);
        println();
    }

    @Override
    public void println(char x) {
        super.print(x);
        println();
    }

    @Override
    public void println(int x) {
        super.print(x);
        println();
    }

    @Override
    public void println(long x) {
        super.print(x);
        println();
    }

    @Override
    public void println(float x) {
        super.print(x);
        println();
    }

    @Override
    public void println(double x) {
        super.print(x);
        println();
    }

    @Override
    public void println(@NotNull char[] x) {
        super.print(x);
        println();
    }

    @Override
    public void println(@Nullable String x) {
        super.print(x);
        println();
    }

    @Override
    public void println(@Nullable Object x) {
        super.print(x);
        println();
    }

    public LegalPrintStream(File file, Charset charset) throws IOException {
        super(file, charset);
    }
}
