package com.zelaux.hjson.no;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonTokenType;

import java.io.IOException;
import java.util.Stack;

public class TestHJsonFlexLexer implements FlexLexer {
    private static final HJsonTokenType objectContextToken = HJsonElementTypes.L_CURLY;
    public final FlexLexer myFlex;
    int myCustomEnd = -1;
    Stack<Object> context = new Stack<>();
    private CharSequence buffer;
    private IElementType previousNonWhiteSpaceToken;
    private int myEnd;

    public TestHJsonFlexLexer(FlexLexer delegate) {
        this.myFlex = delegate;
    }

    private static int indexOf(CharSequence sequence, int startIndex, int endIndex, char c) {
        if (endIndex == -1) endIndex = sequence.length();
        for (int i = startIndex; i < endIndex; i++) {
            if (sequence.charAt(i) == c) return i;
        }
        return -1;
    }

    @Override
    public void yybegin(int state) {
        myFlex.yybegin(state);
    }

    @Override
    public int yystate() {
        return myFlex.yystate();
    }

    @Override
    public int getTokenStart() {
        return myFlex.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        if (myCustomEnd != -1) return myCustomEnd;
        return myFlex.getTokenEnd();
    }

    @Override
    public IElementType advance() throws IOException {
        myCustomEnd = -1;
        IElementType advance = myFlex.advance();
        if (!context.isEmpty()) advance = contextAction(advance);
        return advance;
    }

    private IElementType contextAction(IElementType advance) {
        if (advance == HJsonElementTypes.L_BRACKET || advance == objectContextToken) {
            context.add(advance);
        } else if (advance == HJsonElementTypes.R_BRACKET || advance == HJsonElementTypes.R_CURLY) {
            context.pop();
        } else if (advance == HJsonElementTypes.QUOTELESS_STRING_TOKEN) {
            if (context.get(context.size() - 1) == objectContextToken) {
                int currentStart = myFlex.getTokenStart();
                int currentEnd = myFlex.getTokenEnd();
                int currentState = myFlex.yystate();
                if (previousNonWhiteSpaceToken != HJsonElementTypes.COLON) {
                    int spaceIndex = indexOf(buffer, currentStart, currentEnd, ' ');
                    if (spaceIndex == -1) {//Can be member name
                        advance = HJsonElementTypes.MEMBER_NAME;
                    }
                } else {
                    try {
                        while(true){
                            IElementType nextToken = myFlex.advance();
                            int nextStart = myFlex.getTokenStart();
                            int nextEnd = myFlex.getTokenEnd();
                             if(nextToken==TokenType.WHITE_SPACE) {
                                 int index = indexOf(buffer,nextStart,nextEnd, '\n');
                                 if(index!=-1)break;
                             }

                            myCustomEnd=nextEnd;
                            currentEnd=nextEnd;
                        }

                    } catch (IOException e) {
                        myFlex.reset(buffer, currentEnd, myEnd, currentState);
                    }
                }
            }
        }
        if (advance != TokenType.WHITE_SPACE) {
            previousNonWhiteSpaceToken = advance;
        }
        return advance;
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        myFlex.reset(buf, start, end, initialState);
        this.myEnd = end;
        context.clear();
        context.add(objectContextToken);
        previousNonWhiteSpaceToken = null;
        this.buffer = buf;
    }
}
