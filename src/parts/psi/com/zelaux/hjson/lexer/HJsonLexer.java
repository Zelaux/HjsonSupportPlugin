package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson._HJsonLexer;

public class HJsonLexer extends FlexAdapter {
    public HJsonLexer() {
        super(new HJsonFlexLexer(new _HJsonLexer()));
    }

}
