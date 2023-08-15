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
                "\n" +
                        "property: 11\n" +
                        "array: [\n" +
                        "12\n" +
                        "34\n" +
                        "45\n" +
                        "567\n" +
                        "]\n" +
                        "object: {\n" +
                        "one: 0\n" +
                        "two: {\n" +
                        "\"hmm\\n\": it\n" +
                        "}\n" +
                        "three: 2\n" +
                        "}\n" +
                        "version: 0.0,\n" +
                        "boolean: false\n" +
                        "boolean2: true\n" +
                        "number: 013213e-1\n" +
                        "strings: [\n" +
                        "\"double_quoted\"\n" +
                        "'single_quoted'\n" +
                        "no_qouted\n" +
                        "]\n" +
                        "          name             :    {\n" +
                        "              obj: it\n" +
                        "          },\n" +
                        "  field: 123\n" +
                        "          array: [\n" +
                        "\n" +
                        "     123,\"bvad\",dawd,'ddawd'\n" +
                        "            '''\n" +
                        "            multiline\n" +
                        "            '''\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "    ]", lexer);
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
            System.out.print(type + "(" + replace + ") ");
            if (!original.equals(replace)) {
                System.out.println();
            }
        }
//        Assert.fail();
    }
}
