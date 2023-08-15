package com.zelaux.hjson.no;

import com.intellij.lang.impl.TokenSequence;
import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.lexer.HJsonLexer;
import org.junit.Test;

import java.util.regex.Pattern;

@SuppressWarnings({"UnstableApiUsage", "NewClassNamingConvention"})
public class LexerPreview {

    @Test
    public void test() {
        Lexer lexer = new HJsonLexer();
        TokenSequence sequence = TokenSequence.performLexing(
                "{\"property\": 11, \"array\": [12, 34, 45, 567], \"object\": {\"one\": 0, \"two\": {\"hmm\\n\": \"it\"}, \"three\": 2}, \"version\": 0.0, \"boolean\": false, \"boolean2\": true, \"number\": 013213e-1, \"strings\": [\"double_quoted\", 'single_quoted', \"no qouted\"], \"name\": {\"obj\": \"it\"}, \"field\": 123, \"array\": [123, \"bvad\", \"dawd,\", 'ddawd', \"multiline1\\nmultiline2\\nmultiline3\\nmultiline4\"]}", lexer);
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
