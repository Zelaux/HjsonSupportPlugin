package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import com.zelaux.hjson._HJsonLexer;
import com.zelaux.hjson.string._HJsonStringLexer;
import org.jetbrains.annotations.NotNull;

public class HJsonStringLexer extends FlexAdapter {

    public HJsonStringLexer() {
        super(new _HJsonStringLexer());
    }

    public _HJsonStringLexer getFlex() {
        return (_HJsonStringLexer) super.getFlex();
    }
    @Override
    public @NotNull String getTokenText() {

        return getFlex().yytext().toString();
    }

    @Override
    public int getTokenStart() {
        super.getTokenStart();
        return getFlex().getTokenStart();
    }

}
