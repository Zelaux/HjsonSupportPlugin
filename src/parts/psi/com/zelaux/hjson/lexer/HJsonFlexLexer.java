package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonTokenType;
import com.zelaux.hjson.psi.HJsonTokens;

import java.io.IOException;
import java.util.Stack;

import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.zelaux.hjson.HJsonElementTypes.*;

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
        return currentToken != WHITE_SPACE && !HJsonTokens.COMMENTARIES.contains(currentToken);
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
        if (currentToken == NUMBER_TOKEN) {
            try {
                IElementType next0 = myFlex.advance();
                if (next0 == WHITE_SPACE) {
                    int i = indexOf(buffer, myFlex.getTokenStart(), myFlex.getTokenEnd(), '\n');
                    if (i == -1) {
                        IElementType next1 = myFlex.advance();
                        if (next1 == QUOTELESS_STRING_TOKEN) {
                            currentToken = next1;
                            currentEnd = myCustomEnd = myFlex.getTokenEnd();
                        }
                    }

//                    Pattern.compile("[\\0000]")
                }
                if (currentToken == NUMBER_TOKEN) {
                    backTo(currentEnd, currentState);
                }
            } catch (IOException e) {
                backTo(currentEnd, currentState);
            }
        }
        if (isObjectContext() && isMemberNameContext()) {
            if (// && indexOf(buffer, currentStart, currentEnd, ' ') == -1
                    currentToken == DOUBLE_QUOTED_STRING_TOKEN
                            || currentToken == SINGLE_QUOTED_STRING_TOKEN) {
                currentToken = MEMBER_NAME;
            }
            boolean wasQuoteless = currentToken == QUOTELESS_STRING_TOKEN;
            if (currentToken == NUMBER_TOKEN || wasQuoteless) {
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
                if (!isMain) currentToken = MEMBER_NAME;
            }
            if (wasQuoteless && currentToken != QUOTELESS_STRING_TOKEN) {
                boolean backTo = true;
                try {
                    while (true) {
                        IElementType next0 = myFlex.advance();
                        if (next0 == null) break;
                        if (next0 == WHITE_SPACE) {
                            if (indexOf(buffer, myFlex.getTokenStart(), myFlex.getTokenEnd(), '\n') != -1) break;
                            IElementType next1 = myFlex.advance();
                            if (next1 == null) break;
                            if (next1 == QUOTELESS_STRING_TOKEN) {
                                currentToken = QUOTELESS_STRING_TOKEN;
                                currentEnd = myFlex.getTokenEnd();
                                currentState = myFlex.yystate();
                            } else {
                                break;
                            }
                        } else if (next0 == COLON /*&& context.size()!=1*/) {
                            if (context.size() > 1) {
                                currentToken = MEMBER_NAME;
                            } else {
                                boolean valueIsQuotelesss = false;
                                int state=0;
                                while (state>=0) {

                                    IElementType next1 = myFlex.advance();
                                    if (next1 == null) break;
                                    switch (state) {
                                        case 0:
                                            if (next1 == WHITE_SPACE) continue;
                                            state++;
                                            if (next1 == QUOTELESS_STRING_TOKEN) {
                                                valueIsQuotelesss = true;
                                            }
                                            break;
                                        case 1:
                                            if(next1==COMMA || next1==WHITE_SPACE) {
                                                if (!valueIsQuotelesss && next1 == COMMA) {
                                                    state = -1;
                                                } else if (valueIsQuotelesss && next1 == WHITE_SPACE && indexOf(buffer, myFlex.getTokenStart(), myFlex.getTokenEnd(), '\n') != -1) {
                                                    state = -1;
                                                }
                                            } else{
                                                valueIsQuotelesss=true;
                                            }
                                            break;
                                    }
                                }
                                if(state==-1){
                                    currentToken=MEMBER_NAME;
                                }
                            }
                            break;
                        } else break;
                    }
                } catch (IOException ignore) {
                }
                if (backTo) backTo(currentEnd, currentState);
            }

        }
        if (currentToken == QUOTELESS_STRING_TOKEN) {

            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    IElementType nextToken = myFlex.advance();

                    if (nextToken == null) //noinspection DataFlowIssue
                        throw null;
                    int nextStart = myFlex.getTokenStart();
                    int nextEnd = myFlex.getTokenEnd();
                    if (nextToken == WHITE_SPACE) {
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
                if (getTokenEnd() >= start || advance == null) break;
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
