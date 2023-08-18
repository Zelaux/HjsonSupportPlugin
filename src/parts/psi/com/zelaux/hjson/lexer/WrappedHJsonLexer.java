package com.zelaux.hjson.lexer;

import com.zelaux.hjson._HJsonLexer;

import java.io.IOException;

public class WrappedHJsonLexer extends _HJsonLexer {
    @Override
    public void reset(CharSequence buffer, int start, int end, int initialState) {
        super.reset(buffer, 0, end, initialState);
        this.previuseNonEmptyToken = null;
        this.stateStack.clear();
        try {
            while (getZzMarkedPos() < start) advance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
