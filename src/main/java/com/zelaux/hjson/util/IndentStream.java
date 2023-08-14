package com.zelaux.hjson.util;

import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class IndentStream extends PrintStream {
    public static final byte[] systemSeparatorBytes;
    public static final byte nextLineByte = 10;
    public static final int nextLineIndex;

    static {

        systemSeparatorBytes = System.lineSeparator().getBytes(Charsets.UTF_8);
        int found = -1;
        for (int i = 0; i < systemSeparatorBytes.length; i++) {
            if (systemSeparatorBytes[i] == '\n') {
                found = i;
            }
        }
        nextLineIndex = found;
    }

    public final byte[] indentBytes;
    public String separator;
    private int indentAmount;
    private boolean wasNextLine;
    private byte[] byteSeq;

    public IndentStream(@NotNull PrintStream printStream, String indentValue) {
        super(printStream);
        this.separator = System.lineSeparator();
        this.indentBytes = indentValue.getBytes(Charsets.UTF_8);
        byteSeq = new byte[indentBytes.length * 8];
        for (int i = 0; i < byteSeq.length; i++) {
            byteSeq[i] = indentBytes[i % indentBytes.length];
        }
    }

    public IndentStream(PrintStream printStream) {
        //noinspection UnnecessaryUnicodeEscape
        this(printStream, "\u0020\u0020\u0020\u0020");

    }

    public void increaseIndent() {
        indentAmount++;
        if (indentBytes.length * indentAmount > byteSeq.length) {
            int newIndentAmount = (int) (indentAmount * 1.75);
            byte[] newArr = new byte[newIndentAmount * indentBytes.length];
            System.arraycopy(byteSeq, 0, newArr, 0, byteSeq.length);
            int deltaAmount = newIndentAmount - byteSeq.length / indentBytes.length;
            for (int i = 0; i < deltaAmount; i++) {
                System.arraycopy(indentBytes, 0, newArr, byteSeq.length + i * indentBytes.length, indentBytes.length);
            }
            this.byteSeq = newArr;
        }

    }

    public void decreaseIndent() {
        if (this.indentAmount == 0) {
            throw new IllegalArgumentException("Nothing to decrease");
        } else {
            this.indentAmount += -1;
        }
    }

    public final void indent(@NotNull Runnable block) {
        Intrinsics.checkNotNullParameter(block, "block");
        this.increaseIndent();

        try {
            block.run();
        } finally {
            this.decreaseIndent();
        }

    }


    public void write(byte[] buf, int off, int len) {
        int prevIndex = 0;
        if (wasNextLine) {
            super.write(this.byteSeq, 0, this.indentAmount * indentBytes.length);
            wasNextLine = false;
        }

        int i = off;

        for (int var6 = len; i < var6; ++i) {
            boolean placeIndent = false;
            int j;
            int var9;
            if (buf[i] == ArraysKt.last(systemSeparatorBytes) && i - ArraysKt.getLastIndex(systemSeparatorBytes) >= 0) {
                placeIndent = true;
                j = 0;

                for (var9 = systemSeparatorBytes.length; j < var9; ++j) {
                    if (buf[i - ArraysKt.getLastIndex(systemSeparatorBytes) + j] != systemSeparatorBytes[j]) {
                        placeIndent = false;
                        break;
                    }
                }
            }

            if (buf[i] == 10 && i - nextLineIndex >= 0) {
                j = 0;

                for (var9 = systemSeparatorBytes.length; j < var9; ++j) {
                    if (buf[i - nextLineIndex + j] != systemSeparatorBytes[j]) {
                        placeIndent = true;
                        break;
                    }
                }
            }

            if (placeIndent) {
                super.write(buf, off + prevIndex, i - prevIndex + 1);
                if (i < len - 1) {
                    super.write(byteSeq, 0, indentAmount * indentBytes.length);
                } else {
                    wasNextLine = true;
                }

                prevIndex = i + 1;
            }
        }

        super.write(buf, off + prevIndex, len - prevIndex);
    }

    public void println() {
        this.newLine();
    }

    public void println(boolean x) {
        super.print(x);
        this.newLine();
    }

    public void println(char x) {
        super.print(x);
        this.newLine();
    }

    public void println(int x) {
        super.print(x);
        this.newLine();
    }

    public void println(long x) {
        super.print(x);
        this.newLine();
    }

    public void println(float x) {
        super.print(x);
        this.newLine();
    }

    public void println(double x) {
        super.print(x);
        this.newLine();
    }

    public void println(@NotNull char[] x) {
        Intrinsics.checkNotNullParameter(x, "x");
        super.print(x);
        this.newLine();
    }

    public void println(@Nullable String x) {
        super.print(x);
        this.newLine();
    }

    public void println(@Nullable Object x) {
        super.print(x);
        this.newLine();
    }

    public final void newLine() {
        super.print(this.separator);
    }

    public final byte[] byteSeq() {
        return this.byteSeq;
    }

    public void reset() {
        wasNextLine = false;
        indentAmount = 0;
    }

    public int currentIndent() {
        return indentAmount;
    }
}
