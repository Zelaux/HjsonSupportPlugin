package com.zelaux.hjson.no;

import com.intellij.lang.impl.TokenSequence;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson._HJsonLexer;
import com.zelaux.hjson.lexer.HJsonLexer;
import org.junit.Test;

import java.util.regex.Pattern;

@SuppressWarnings({"UnstableApiUsage", "NewClassNamingConvention"})
public class LexerPreview {

    @Test
    public void test() {
        Lexer lexer = new HJsonLexer();
        TokenSequence sequence = TokenSequence.performLexing(
                "name d: hh", lexer);
        String text = String.valueOf(sequence.getTokenizedText());
        for (int i = 0; i < sequence.getTokenCount(); i++) {
            IElementType type = sequence.getTokenType(i);
            int tokenEnd = sequence.getTokenEnd(i);
//            System.out.print(type);
            int tokenStart = sequence.getTokenStart(i);
            int i1 = text.indexOf('\n', tokenStart);
//            if (i1 >= 0 && i1 < tokenEnd) System.out.println();
            String original = text.substring(tokenStart, tokenEnd);
            String replace = original.replace("\n", "\\n");
            System.out.print(type + "(" + replace + ")\n");
            if (!original.equals(replace)) {
                System.out.println();
            }
        }
//        Assert.fail();
    }
}
