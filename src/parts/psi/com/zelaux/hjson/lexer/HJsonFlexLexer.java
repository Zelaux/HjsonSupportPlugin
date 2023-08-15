package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonTokenType;
import com.zelaux.hjson.psi.HJsonTokens;

import java.io.IOException;
import java.util.Stack;

class HJsonFlexLexer implements FlexLexer {
    private static final HJsonTokenType objectContextToken = HJsonElementTypes.L_CURLY;
    public final FlexLexer myFlex;
    int myCustomEnd = -1, myCustomStart = -1;
    Stack<Object> context = new Stack<>();
    private CharSequence buffer;
    private IElementType previousNonEmptyToken;
    private int myEnd;

    public HJsonFlexLexer(FlexLexer delegate) {
        this.myFlex = delegate;
    }

    private static int indexOf(CharSequence sequence, int startIndex, int endIndex, char c) {
        if (endIndex == -1) endIndex = sequence.length();
        for (int i = startIndex; i < endIndex; i++) {
            if (sequence.charAt(i) == c) return i;
        }
        return -1;
    }

    private static boolean isNotEmpty(IElementType currentToken) {
        return currentToken != TokenType.WHITE_SPACE && !HJsonTokens.COMMENTARIES.contains(currentToken);
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
        if (myCustomStart != -1) return myCustomStart;
        return myFlex.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        if (myCustomEnd != -1) return myCustomEnd;
        return myFlex.getTokenEnd();
    }

    @Override
    public IElementType advance() throws IOException {
        myCustomEnd = myCustomStart = -1;
        IElementType advance = myFlex.advance();
        myCustomStart = myFlex.getTokenStart();
        if (!context.isEmpty()) {
            if (advance == HJsonElementTypes.L_BRACKET || advance == objectContextToken) {
                context.add(advance);
            } else if (advance == HJsonElementTypes.R_BRACKET || advance == HJsonElementTypes.R_CURLY) {
                context.pop();
            }
        }
        if (!context.isEmpty()) advance = contextAction(advance);
        return advance;
    }

    private IElementType contextAction(IElementType currentToken) {
        int currentStart = myFlex.getTokenStart();
        int currentEnd = myFlex.getTokenEnd();
        int currentState = myFlex.yystate();
        if (currentToken == HJsonElementTypes.NUMBER_TOKEN) {
            try {
                IElementType next0 = myFlex.advance();
                if (next0 == TokenType.WHITE_SPACE) {
                    int i = indexOf(buffer, myFlex.getTokenStart(), myFlex.getTokenEnd(), '\n');
                    if (i == -1) {
                        IElementType next1 = myFlex.advance();
                        if (next1 == HJsonElementTypes.QUOTELESS_STRING_TOKEN) {
                            currentToken = next1;
                            currentEnd = myCustomEnd = myFlex.getTokenEnd();
                        }
                    }

//                    Pattern.compile("[\\0000]")
                }
                if (currentToken == HJsonElementTypes.NUMBER_TOKEN) {
                    backTo(currentEnd, currentState);
                }
            } catch (IOException e) {
                backTo(currentEnd, currentState);
            }
        }
        if (isObjectContext() && isMemberNameContext()) {
            if (currentToken == HJsonElementTypes.QUOTELESS_STRING_TOKEN// && indexOf(buffer, currentStart, currentEnd, ' ') == -1
                    || currentToken == HJsonElementTypes.DOUBLE_QUOTED_STRING_TOKEN
                    || currentToken == HJsonElementTypes.SINGLE_QUOTED_STRING_TOKEN) {
                currentToken = HJsonElementTypes.MEMBER_NAME;
            }
            if (currentToken == HJsonElementTypes.NUMBER_TOKEN) {
                boolean isMain = previousNonEmptyToken == null;
                if (isMain) {
                    try {
                        while (isMain) {
                            IElementType next = myFlex.advance();
                            if (next == null) break;
                            if (isNotEmpty(next)) isMain = false;
                        }
                    } catch (IOException ignore) {
                    }
                    backTo(currentEnd, currentState);
                }
                if (!isMain) currentToken = HJsonElementTypes.MEMBER_NAME;
            }
        } else if (currentToken == HJsonElementTypes.QUOTELESS_STRING_TOKEN) {

            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    IElementType nextToken = myFlex.advance();

                    if (nextToken == null) //noinspection DataFlowIssue
                        throw null;
                    int nextStart = myFlex.getTokenStart();
                    int nextEnd = myFlex.getTokenEnd();
                    if (nextToken == TokenType.WHITE_SPACE) {
                        int index = indexOf(buffer, nextStart, nextEnd, '\n');
                        if (index != -1) {
                            myCustomEnd = index;
                            currentEnd = myCustomEnd;
                            //noinspection DataFlowIssue
                            throw null;
                        }
                    }

                    myCustomEnd = nextEnd;
                    currentEnd = nextEnd;
                    currentState = myFlex.yystate();
                }

            } catch (IOException | NullPointerException e) {
                backTo(currentEnd, currentState);
            }
        }
        if (isNotEmpty(currentToken)) {
            previousNonEmptyToken = currentToken;
        }
        return currentToken;
    }

    private boolean isMemberNameContext() {
        return previousNonEmptyToken != HJsonElementTypes.COLON;
    }

    private boolean isObjectContext() {
        return context.get(context.size() - 1) == objectContextToken;
    }

    private void backTo(int position, int state) {
        myFlex.reset(buffer, position, myEnd, state);
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        myFlex.reset(buf, 0, end, initialState);
        this.myEnd = end;
        myCustomStart = myCustomEnd = -1;
        previousNonEmptyToken = null;
        this.buffer = buf;

        context.clear();
        context.add(objectContextToken);
        if (start == 0) return;
        try {
            while (true) {
                IElementType advance = advance();
                if (getTokenEnd()>=start || advance==null) break;
            }
        } catch (IOException ignored) {
        }
    /*    for (int i = 0; i < start; i++) {
            char c = buf.charAt(i);
            if (!Character.isSpaceChar(c)) {
                if (c == ':') {
                    previousNonEmptyToken = HJsonElementTypes.COLON;
                } else {
                    previousNonEmptyToken = TokenType.WHITE_SPACE;
                }
            }
            if (c == '[') {
                context.add(HJsonElementTypes.L_BRACKET);
            } else if (c == '{') {
                context.add(HJsonElementTypes.L_CURLY);
            } else if (c == ']' || c == '}') {
                context.pop();
            }
        }*/
    }
}
