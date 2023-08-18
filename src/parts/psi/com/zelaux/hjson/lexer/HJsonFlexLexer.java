package com.zelaux.hjson.lexer;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson._HJsonLexer;
import com.zelaux.hjson.psi.HJsonTokens;

import java.io.IOException;
import java.util.regex.Matcher;

import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.zelaux.hjson.HJsonElementTypes.*;

public class HJsonFlexLexer extends DelegateLexer {

    private static TokenSet memberName = TokenSet.create(
            NUMBER_TOKEN, DOUBLE_QUOTED_STRING_TOKEN, SINGLE_QUOTED_STRING_TOKEN, QUOTELESS_STRING_TOKEN,
            TRUE,FALSE,NULL
    );
    final CacheLexer delegate;
    CharSequence buffer;
    private IElementType previousNonEmptyToken = null;

    public HJsonFlexLexer(WrappedHJsonLexer delegate) {
        super(new CacheLexer(delegate));
        this.delegate = (CacheLexer) super.delegate;
    }

    private static boolean isNotEmpty(IElementType advance) {
        return advance != WHITE_SPACE && !HJsonTokens.comments.contains(advance);
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        super.reset(buffer = buf, 0, end, initialState == 0 ? _HJsonLexer.IN_OBJECT : initialState);
        previousNonEmptyToken = null;
        try {
            while (getTokenEnd() < start) advance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public IElementType advance() throws IOException {
        int yystate = yystate();
        CacheLexer.TokenEntry entry = delegate.removeFirst();
        if (entry == null) return null;
        IElementType advance = entry.token;
      /*  if (((String)((_HJsonLexer)delegate.delegate).zzBuffer).substring(entry.start,entry.end).equals(":")) {
            System.out.println(entry.token);
        }*/
        if (yystate == _HJsonLexer.IN_OBJECT) {
            boolean isMain = previousNonEmptyToken == null;
            if (isMain) {
                while (isMain) {
                    IElementType next = delegate.advance();
                    if (next == null) break;
                    if (isNotEmpty(next)) {
                        isMain = false;
                    }
                }
                delegate.gotoOffset(entry.indexWithOffset);
            }
            if (!isMain && memberName.contains(advance)) {
                advance = MEMBER_NAME;
            } else if (isMain && advance == QUOTELESS_STRING_TOKEN) {
                Matcher matcher = _HJsonLexer.numberPattern.matcher(buffer);
                if (matcher.find()) {
                    if (matcher.start() == getTokenStart() && matcher.end() < getTokenEnd()) {
                        int end = matcher.end();

                        while (end < buffer.length() && buffer.charAt(end) == ' ') {
                            end++;
                        }

                        if (end < buffer.length() && buffer.charAt(end) == ',') {
                            WrappedHJsonLexer lexer = (WrappedHJsonLexer) delegate.delegate;
                            lexer.zzMarkedPos(end+1);
                            advance = MEMBER_NAME;
                        }
                    }
                }
            }

        }
        if (isNotEmpty(advance)) {
            previousNonEmptyToken = advance;
        }
        return advance;
    }
}
