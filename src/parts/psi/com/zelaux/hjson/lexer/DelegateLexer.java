package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import java.io.IOException;

public class DelegateLexer implements FlexLexer {
    final FlexLexer delegate;

    public DelegateLexer(FlexLexer delegate) {
        this.delegate = delegate;
    }


    @Override
    public void yybegin(int state) {
        delegate.yybegin(state);
    }

    @Override
    public int yystate() {
        return delegate.yystate();
    }

    @Override
    public int getTokenStart() {
        return delegate.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        return delegate.getTokenEnd();
    }

    @Override
    public IElementType advance() throws IOException {
        return delegate.advance();
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        delegate.reset(buf, start, end, initialState);
    }
}
