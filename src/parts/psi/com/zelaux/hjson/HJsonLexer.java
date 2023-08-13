package com.zelaux.hjson;

import com.intellij.lexer.FlexAdapter;

public class HJsonLexer extends FlexAdapter {
    public HJsonLexer() {
        super(new _HJsonLexer());
    }
}
