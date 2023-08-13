package com.zelaux.hjson.lexer;

import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.tree.IElementType;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Objects;

public class HJsonStringLexerTest extends TestCase {
    public HJsonStringLexerTest() {
        super();
    }

    public void testParsing() {

        HJsonStringLexer lexer = new HJsonStringLexer();
        lexer.start("{\n" +
                    "  ggg: 123,\n" +
                    "  fals: bbb\n" +
                    "  fals: \"\\uffff \\d\\n\\uff\"\n" +
                    "}");
        System.out.println("begin");
        LexerResultEntry[] entries = {
                new LexerResultEntry(StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN, "\\uffff", 35, 41),
                new LexerResultEntry(StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN, "\\d", 42, 44),
                new LexerResultEntry(StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN, "\\n", 44, 46),
                new LexerResultEntry(StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN, "\\uff", 46, 50),
        };
        int i = 0;
        while (lexer.getTokenType() != null) {


            LexerResultEntry entry = new LexerResultEntry(
                    lexer.getTokenType(),
                    lexer.getTokenText(),
                    lexer.getTokenStart(),
                    lexer.getTokenEnd()

            );
            Assert.assertTrue("Unexpected token", i < entries.length);
            LexerResultEntry expectedEntry = entries[i];
            Assert.assertNotEquals("String lexer returns empty token(" + lexer.getTokenStart() + "," + lexer.getTokenEnd() + ")", "", entry.text);
            Assert.assertEquals(expectedEntry,entry);
            System.out.println("\t"+entry);
            i++;
            lexer.advance();
        }
        System.out.println("end");

    }

    static class LexerResultEntry {
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

        @Override
        public String toString() {
            return "'"+text + "' (" + tokenType + "[" + tokenStart + ':' + tokenEnd + "])";
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
}