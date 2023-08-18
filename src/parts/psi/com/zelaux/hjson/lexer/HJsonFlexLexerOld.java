package com.zelaux.hjson.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonTokenType;
import com.zelaux.hjson.psi.HJsonTokens;

import java.io.IOException;
import java.util.Stack;

import static com.intellij.psi.TokenType.WHITE_SPACE;

class HJsonFlexLexerOld implements FlexLexer {
    private static final HJsonTokenType objectContextToken = HJsonElementTypes.L_CURLY;
    public final CacheLexer myFlex;
    int myCustomEnd = -1, myCustomStart = -1;
    Stack<Object> context = new Stack<>();
    private CharSequence buffer;
    private IElementType previousNonEmptyToken;
    private int myEnd;

    public HJsonFlexLexerOld(FlexLexer delegate) {
        this.myFlex = new CacheLexer(delegate);
    }

    private static boolean isNotEmpty(IElementType currentToken) {
        return currentToken != WHITE_SPACE && !HJsonTokens.comments.contains(currentToken);
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
        CacheLexer. TokenEntry advance = myFlex.removeFirst();
        myCustomStart = myFlex.getTokenStart();
        IElementType currentToken =advance==null?null: advance.token;
        if (!context.isEmpty()) {
            if (currentToken == HJsonElementTypes.L_BRACKET || currentToken == objectContextToken) {
                context.add(currentToken);
            } else if (currentToken == HJsonElementTypes.R_BRACKET || currentToken == HJsonElementTypes.R_CURLY) {
                context.pop();
            }
        }
//        if (!context.isEmpty() && advance!=null) currentToken = contextAction(advance);
        return currentToken;
    }

    private IElementType nextToken() throws IOException {
        return myFlex.advance();
    }

    /*private IElementType contextAction(CacheLexer.TokenEntry entry) {
        IElementType currentToken = entry.token;
        boolean isMain = isMain();
        int currentStart = myFlex.getTokenStart();
//        int currentEnd = myFlex.getTokenEnd();
//        int currentState = myFlex.yystate();
        if (currentToken == NUMBER_TOKEN) {
            try {
                IElementType next0 = nextToken();
                if (next0 == WHITE_SPACE) {
                    int startIndex = myFlex.getTokenStart();
                    int endIndex = myFlex.getTokenEnd();
                    int i = Strings.indexOf(buffer, '\n', startIndex, endIndex);
                    if (i == -1) {
                        IElementType next1 = nextToken();
                        if (next1 == QUOTELESS_STRING_TOKEN) {
                            currentToken = next1;
                            currentEnd = myCustomEnd = myFlex.getTokenEnd();
                        }
                    }

//                    Pattern.compile("[\\0000]")
                }
                if (currentToken == NUMBER_TOKEN) {
                    backTo(entry,0);
                }
            } catch (IOException e) {
                backTo(entry,0);
            }
        }

        boolean isMemberNameContext = isObjectContext() && isMemberNameContext();
        if (currentToken == NUMBER_TOKEN) {
            try {
                int state = isMemberNameContext ? -1 : 0;
                while (state >= 0) {
                    IElementType next = nextToken();
                    if (next == null) break;
                    if (HJsonTokens.comments.contains(next)) {
                        state = -1;
                    } else if (next == WHITE_SPACE) {
                        boolean isNextLine = indexOf(myFlex.getTokenStart(), myFlex.getTokenEnd(), '\n') != -1;
                        if (isNextLine) {
                            state = -1;
                        } else {
                            state = 1;
                        }
                    } else {
                        if (next == COMMA) {
                            state = 0;
                            break;
                        }
                        state = -1;
                    }
                }
                if (state == -1) {
                    currentToken = QUOTELESS_STRING_TOKEN;
                }
            } catch (IOException ignore) {
            }
            backTo(currentEnd, currentState);

        } else*//* if (wasQuoteless) {
            boolean isMain = previousNonEmptyToken == null;
            if (isMain) {
                try {
                    while (isMain) {
                        IElementType next = nextToken();
                        if (next == null) break;
                        if (isNotEmpty(next)) isMain = false;
                    }
                } catch (IOException ignore) {
                }
                backTo(currentEnd, currentState);
            }
            if (!isMain) currentToken = MEMBER_NAME;
        }*//*
        if (currentToken == MEMBER_NAME) {
            boolean backTo = true;
            int tokenOffset=0;
            try {
                int currentOffset=0;
                while (true) {
                    IElementType next0 = nextToken();
                    currentOffset++;
                    if (next0 == null) break;
                    if (next0 == WHITE_SPACE) {
                        int startIndex = myFlex.getTokenStart();
                        int endIndex = myFlex.getTokenEnd();
                        if (Strings.indexOf(buffer, '\n', startIndex, endIndex) != -1) break;
                        IElementType next1 = nextToken();
                        currentOffset++;
                        if (next1 == null) break;
                        if (next1 == QUOTELESS_STRING_TOKEN ) {
                            currentToken = QUOTELESS_STRING_TOKEN;
//                            currentEnd = myFlex.getTokenEnd();
                            tokenOffset=currentOffset;
//                            currentState = myFlex.yystate();
                        } else {
                            break;
                        }
                    } else if (next0 == COLON *//*&& context.size()!=1*//*) {
                        if (context.size() > 1) {
                            currentToken = MEMBER_NAME;
                        } else {
                            boolean valueIsQuotelesss = false;
                            int __state = 0;
                            while (__state >= 0) {

                                IElementType next1 = nextToken();
                                currentOffset++;
                                if (next1 == null) break;
                                switch (__state) {
                                    case 0:
                                        if (next1 == WHITE_SPACE) continue;
                                        __state++;
                                        if (next1 == QUOTELESS_STRING_TOKEN) {
                                            valueIsQuotelesss = true;
                                        }
                                        break;
                                    case 1:
                                        if (next1 == COMMA || next1 == WHITE_SPACE) {
                                            if (!valueIsQuotelesss && next1 == COMMA) {
                                                __state = -1;
                                            } else {
                                                if (valueIsQuotelesss && next1 == WHITE_SPACE) {
                                                    int startIndex = myFlex.getTokenStart();
                                                    int endIndex = myFlex.getTokenEnd();
                                                    if (Strings.indexOf(buffer, '\n', startIndex, endIndex) != -1) {
                                                        __state = -1;
                                                    }
                                                }
                                            }
                                        } else {
                                            valueIsQuotelesss = true;
                                        }
                                        break;
                                }
                            }
                            if (__state == -1) {
                                currentToken = MEMBER_NAME;
                            }
                        }
                        break;
                    } else break;
                }
            } catch (IOException ignore) {
            }
            if (backTo) backTo(entry,tokenOffset);
        }

        if (currentToken == QUOTELESS_STRING_TOKEN) {

            try {
                int state=0;
                while (true) {
                    IElementType nextToken = nextToken();

                    if (nextToken == null) break;
                    int nextStart = myFlex.getTokenStart();
                    int nextEnd = myFlex.getTokenEnd();
                    if (nextToken == WHITE_SPACE) {
                        int index = Strings.indexOf(buffer, '\n', nextStart, nextEnd);
                        if (index != -1) {
                            myCustomEnd = index;
                            currentEnd = myCustomEnd;
                            myFlex.reset(buffer,);
                            //noinspection DataFlowIssue
                            break;
                        }
                    } else if(state==1){
                        myCustomEnd
                    }

                    myCustomEnd = nextEnd;
                    currentEnd = nextEnd;
                    currentState = myFlex.yystate();
                }

            } catch (IOException e) {
                backTo(currentEnd, currentState);
            }
        }
        if (isMemberNameContext)
            if (currentToken == QUOTELESS_STRING_TOKEN ||
                    currentToken == DOUBLE_QUOTED_STRING_TOKEN ||
                    currentToken == SINGLE_QUOTED_STRING_TOKEN) {
                currentToken = MEMBER_NAME;
            }
        if (isNotEmpty(currentToken)) {
            previousNonEmptyToken = currentToken;
        }
        return currentToken;
    }*/

    private boolean isMain() {
        if (previousNonEmptyToken != null) return false;
        int currentEnd = myFlex.getTokenEnd();
        int currentState = myFlex.yystate();
       throw null;
    }

    private int indexOf(int start, int end, char c) {
        return Strings.indexOf(buffer, c, start, end);
    }

    private boolean isMemberNameContext() {
        return previousNonEmptyToken != HJsonElementTypes.COLON;
    }

    private boolean isObjectContext() {
        return context.get(context.size() - 1) == objectContextToken;
    }

    void backTo(CacheLexer.TokenEntry entry,int offset) {
        myFlex.gotoOffset(entry.indexWithOffset+1+offset);
    }

    void backTo(int position, int state) throws IllegalAccessException{
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
