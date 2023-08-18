package com.zelaux.hjson.lexer;

import com.intellij.psi.tree.IElementType;

import java.util.Objects;

public class LexerResultEntry {
    public final IElementType tokenType;
    public final String text;
    public final int tokenStart;
    public final int tokenEnd;

    public LexerResultEntry(IElementType tokenType, String text, int tokenStart, int tokenEnd) {
        this.tokenType = tokenType;
        this.text = text;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;
    }
    public static LexerResultEntry entry(IElementType tokenType, String text, int tokenStart, int tokenEnd) {
        return new LexerResultEntry(tokenType, text, tokenStart, tokenEnd);
    }

    @Override
    public String toString() {
        return "'" + text + "' (" + tokenType + "[" + tokenStart + ':' + tokenEnd + "])";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexerResultEntry that = (LexerResultEntry) o;
        return tokenStart == that.tokenStart && tokenEnd == that.tokenEnd && Objects.equals(tokenType, that.tokenType) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType, text, tokenStart, tokenEnd);
    }
}
