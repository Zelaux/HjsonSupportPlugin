package com.zelaux.hjson.lexer;

import com.zelaux.hjson.HJsonElementTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.util.ArrayList;

import static com.zelaux.hjson.lexer.HJsonRangedLexerTest.RangedTestCase.test;
import static com.zelaux.hjson.lexer.LexerResultEntry.entry;

public class HJsonRangedLexerTest {
    static final RangedTestCase[] cases = {
            test("{0: zero}", 1, 2,
                    entry(HJsonElementTypes.MEMBER_NAME, "0", 1, 2)),
            test("{0: zero[: 0\n1: one: 1\n}", 16, 22),
    };

    public HJsonRangedLexerTest() {
        super();
    }

    @org.junit.Test
    public void testHJsonLexerWithRanges() {

        HJsonLexer lexer = new HJsonLexer();
        for (RangedTestCase rangedTestCase : cases) {
            LexerResultEntry[] entries = rangedTestCase.entries;
            if(entries.length==0){
                ArrayList<LexerResultEntry> tmp=new ArrayList<>();
                lexer.start(rangedTestCase.data);
                while (lexer.getTokenType()!=null){
                    LexerResultEntry entry = lexerResultEntry(lexer);
                    if (entry.tokenStart >= rangedTestCase.start) {
                        if (entry.tokenEnd > rangedTestCase.end) break;
                        tmp.add(entry);
                    }
                    lexer.advance();
                }
                entries=tmp.toArray(entries);
            }
            lexer.start(rangedTestCase.data, rangedTestCase.start, rangedTestCase.end);
            System.out.println("begin");
            int i = 0;
            while (lexer.getTokenType() != null) {
                LexerResultEntry entry = lexerResultEntry(lexer);
                Assert.assertTrue("Unexpected token " + entry, i < entries.length);
                LexerResultEntry expectedEntry = entries[i];
                Assert.assertNotEquals("String lexer returns empty token(" + lexer.getTokenStart() + "," + lexer.getTokenEnd() + ")", "", entry.text);
                Assert.assertEquals(expectedEntry, entry);
                System.out.println("\t" + entry);
                i++;
                lexer.advance();
            }
            if (i < entries.length)
                Assert.fail("Missing tokens " + (entries.length - i - 1) + "(" + rangedTestCase.data + ")");
            System.out.println("end");
        }


    }

    @NotNull
    private static LexerResultEntry lexerResultEntry(HJsonLexer lexer) {
        LexerResultEntry entry = new LexerResultEntry(
                lexer.getTokenType(),
                lexer.getTokenText(),
                lexer.getTokenStart(),
                lexer.getTokenEnd()
        );
        return entry;
    }

    static class RangedTestCase {
        public final String data;
        public final int start;
        public final int end;
        public final LexerResultEntry[] entries;

        RangedTestCase(String data, int start, int end, LexerResultEntry... entries) {
            this.data = data;
            this.start = start;
            this.end = end;
            this.entries = entries;
        }

        static RangedTestCase test(String data, int start, int end, LexerResultEntry... entries) {
            return new RangedTestCase(data, start, end, entries);
        }
    }
}