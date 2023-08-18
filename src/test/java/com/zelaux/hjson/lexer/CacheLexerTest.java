package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson._HJsonLexer;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("NewClassNamingConvention")
public class CacheLexerTest {
    @Test
    public void test1() {
        String placeholder = "";
        LexerResultEntry[] entries = {
                entry(HJsonElementTypes.COMMA, placeholder, 0, 1),
                entry(HJsonElementTypes.NUMBER_TOKEN, placeholder, 1, 2),
                entry(HJsonElementTypes.STRING_LITERAL, placeholder, 2, 3),
        };
        CacheLexer lexer = new CacheLexer(new FlexLexer() {
            int index = -1;

            @Override
            public void yybegin(int state) {

            }

            @Override
            public int yystate() {
                return 0;
            }

            @Override
            public int getTokenStart() {
                return index >= entries.length ? -1 : entries[index].tokenStart;
            }

            @Override
            public int getTokenEnd() {
                return index >= entries.length ? -1 : entries[index].tokenEnd;
            }

            @Override
            public IElementType advance() throws IOException {
                index++;
                if (index < entries.length) System.out.println("new");
                return index >= entries.length ? null : entries[index].tokenType;

            }

            @Override
            public void reset(CharSequence buf, int start, int end, int initialState) {
                index = -1;
            }
        });
        try {
            for (int index = 0; ; index++) {
                CacheLexer.TokenEntry entry = lexer.removeFirst();
                System.out.println(entry);
                if (entry == null) break;

                String mainMessage = "entry[" + (index) + "]";
                assertEquals(mainMessage, entries[index], entry);
                Assert.assertEquals(mainMessage, entries[index], new LexerResultEntry(entry.token, placeholder, lexer.getTokenStart(), lexer.getTokenEnd()));
                int subIndex = index + 1;
                while (true) {
                    CacheLexer.TokenEntry advance = lexer.advanceEntry();
                    if (advance == null) {
                        lexer.gotoOffset(entry.indexWithOffset + 1);
                        break;
                    } else {
                        String subMessage = mainMessage + "->subEntry[" + subIndex + "]";
                        assertEquals(subMessage, entries[subIndex], advance);
                        Assert.assertEquals(subMessage, entries[subIndex], new LexerResultEntry(advance.token, placeholder, lexer.getTokenStart(), lexer.getTokenEnd()));
                    }
                    subIndex++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test2() throws IOException {
        String myText = FileUtil.loadFile(new File("src/test/testData/lexer/cache_lexer.txt"));
        _HJsonLexer rawLexer = new WrappedHJsonLexer();
        rawLexer.reset(myText, 0, myText.length(), _HJsonLexer.IN_OBJECT);
        ArrayList<LexerResultEntry> rawEntries = new ArrayList<>();
        while (true) {
            IElementType advance = rawLexer.advance();
            if (advance == null) break;
            ;
            rawEntries.add(entry(myText, rawLexer, advance));
        }
        CacheLexer lexer = new CacheLexer(rawLexer);
        rawLexer.reset(myText, 0, myText.length(), _HJsonLexer.IN_OBJECT);

        for (int index = 0; ; index++) {
            CacheLexer.TokenEntry entry = lexer.removeFirst();
//            System.out.println("[" + index + "]" + entry);
            if (entry == null) break;

            String mainMessage = "entry[" + (index) + "]";
            assertEquals(mainMessage, rawEntries.get(index), entry);
            checkEntryValues(myText, rawEntries, index, entry, mainMessage);
            checkLexerValues(myText, rawEntries, index, entry, mainMessage, lexer);

            for (int subIndex = index + 1; ; subIndex++) {
                CacheLexer.TokenEntry subEntry = lexer.advanceEntry();
                if (subEntry == null) {
                    lexer.gotoIndex(0);
                    break;
                }
                String subMessage = mainMessage + "->subEntry[" + subIndex + "]";
                assertEquals(subMessage, rawEntries.get(subIndex), subEntry);
                checkEntryValues(myText, rawEntries, subIndex, subEntry, subMessage);
                checkLexerValues(myText, rawEntries, subIndex, subEntry, subMessage, lexer);
            }
        }

    }

    private static void checkEntryValues(String myText, ArrayList<LexerResultEntry> rawEntries, int index, CacheLexer.TokenEntry entry, String mainMessage) {
        Assert.assertEquals(mainMessage + "_entry", rawEntries.get(index), entryFromToken(myText, entry));
    }

    private static void checkLexerValues(String myText, ArrayList<LexerResultEntry> rawEntries, int index, CacheLexer.TokenEntry entry, String mainMessage, CacheLexer lexer) {
        Assert.assertEquals(mainMessage + "_lexer", rawEntries.get(index), entry(myText, lexer, entry.token));
    }

    static LexerResultEntry entry(IElementType tokenType, String text, int tokenStart, int tokenEnd) {
        return new LexerResultEntry(tokenType, text, tokenStart, tokenEnd);
    }

    @NotNull
    private static LexerResultEntry entryFromToken(String myText, CacheLexer.TokenEntry entry) {
        return new LexerResultEntry(entry.token, myText.substring(entry.start, entry.end), entry.start, entry.end);
    }

    @NotNull
    private static LexerResultEntry entry(String myText, FlexLexer rawLexer, IElementType advance) {
        return new LexerResultEntry(advance, myText.substring(rawLexer.getTokenStart(), rawLexer.getTokenEnd()), rawLexer.getTokenStart(), rawLexer.getTokenEnd());
    }

    private static void assertEquals(String message, LexerResultEntry expectedEntry, CacheLexer.TokenEntry actualEntry) {
        Object[] expected = {expectedEntry.tokenType, expectedEntry.tokenStart, expectedEntry.tokenEnd};
        Object[] actual = {actualEntry.token, actualEntry.start, actualEntry.end};
        try {
            Assert.assertArrayEquals(message, expected, actual);
        } catch (AssertionError e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 6];
            System.arraycopy(stackTrace, 5, newStackTrace, 0, newStackTrace.length);
            e.setStackTrace(newStackTrace);
            throw e;

        }
    }
}
