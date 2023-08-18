package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexAdapter;
import com.zelaux.hjson._HJsonLexer;
import com.zelaux.hjson.string._HJsonStringLexer;
import org.jetbrains.annotations.NotNull;

public class HJsonLexer extends FlexAdapter {
    public HJsonLexer() {
        super(new HJsonFlexLexer(new WrappedHJsonLexer()));
    }
//    public _HJsonLexer getFlex() {
//        return (HJsonFlexLexer) super.getFlex();
//    }
    @Override
    public @NotNull String getTokenText() {

        return getBufferSequence().subSequence(getTokenStart(),getTokenEnd()).toString();
    }

    @Override
    public int getTokenStart() {
        super.getTokenStart();
        return getFlex().getTokenStart();
    }
}
