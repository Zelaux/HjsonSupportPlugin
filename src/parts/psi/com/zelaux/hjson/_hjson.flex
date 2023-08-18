package com.zelaux.hjson;

import arc.struct.IntSeq;
import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.psi.HJsonTokens;
import java.io.IOException;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.zelaux.hjson.HJsonElementTypes.*;
import java.util.regex.Matcher;

%%

%{
  protected final IntSeq stateStack=new IntSeq();
  protected _HJsonLexer() {
    this((java.io.Reader)null);
  }
  protected IElementType previuseNonEmptyToken;
%}

%public
%class _HJsonLexer
%implements FlexLexer
%function advanceToken
%type IElementType
%unicode


%{
    @Override
    public IElementType advance() throws java.io.IOException {
        if(stateStack.size<0)return BAD_CHARACTER;
        int prevState= yystate();

        int prevSize=stateStack.size;
        IElementType token= advanceToken();
        if(stateStack.size<0)return BAD_CHARACTER;
        int newState= yystate();
        if(token!=WHITE_SPACE && !HJsonTokens.comments.contains(token)){
            previuseNonEmptyToken=token;
        }
        if(prevState!=IN_MEMBER_VALUE && newState==IN_MEMBER_VALUE && stateStack.size>prevSize){
            //System.out.println("RESET");
            previuseNonEmptyToken=null;
        }
        return token;
    }
    protected int getZzMarkedPos() {
        return zzMarkedPos;
    }

    public void nextState(int state) {
//            System.out.println("add state("+yystate()+"->"+state+") at "+printToken());
        stateStack.add(yystate());
        yybegin(state);
    }

    public void popState() {
        if (stateStack.size <= 0) {
            stateStack.size = -1;
            zzMarkedPos = zzEndRead - zzStartRead;
            return;
        }
        int state = stateStack.pop();
//            System.out.println("pop state("+yystate()+"->"+state+") at "+printToken());
        yybegin(state);
    }

    String printToken() {


        return "'" + com.intellij.openapi.util.text.StringUtil.escapeStringCharacters(String.valueOf(zzBuffer.subSequence(getTokenStart(), getTokenEnd()))) + "' [" + getTokenStart() + ", " + getTokenEnd() + "]";
    }

    public void zzMarkedPos(int zzMarkedPos) {
        this.zzMarkedPos = zzMarkedPos;
    }

    public void zzCurrentPos(int zzCurrentPos) {
        this.zzCurrentPos = zzCurrentPos;
    }

    protected void checkWhiteSpaceInInit() {
        if (yystate() == YYINITIAL) {
            if (newLine()) {
                yybegin(IN_OBJECT);
            }
        }
    }

    protected boolean checkQuitFromMemberValue() {
        return previuseNonEmptyToken != null && newLine();
    }

    protected boolean newLine() {
        return Strings.indexOf(zzBuffer, '\n', getTokenStart(), getTokenEnd()) != -1;
    }

    public static final java.util.regex.Pattern numberPattern = java.util.regex.Pattern.compile("-?(\\d+)(\\.\\d+)?([Ee][+-]?\\d+)?");

    public int indexOf(char c) {
        return StringUtil.indexOf(zzBuffer, c, getTokenStart(), getTokenEnd());
    }

    public static final java.util.regex.Pattern otherPattern = java.util.regex.Pattern.compile("(null|false|true)(\\s*(/\\*([^*]|\\*+[^*/])*(\\*+/)?)?)*,");

    public IElementType returnNumberOrQuotelessString() {
        Matcher otherPattern = _HJsonLexer.otherPattern.matcher(zzBuffer);
        if (otherPattern.find(getTokenStart()) && otherPattern.start() == getTokenStart()) {
            if (StringUtil.startsWith(zzBuffer, getTokenStart(), "false")) {
                zzMarkedPos = getTokenStart() + 5;
                return FALSE;
            } else if (StringUtil.startsWith(zzBuffer, getTokenStart(), "true")) {
                zzMarkedPos = getTokenStart() + 4;
                return TRUE;
            } else if (StringUtil.startsWith(zzBuffer, getTokenStart(), "null")) {
                zzMarkedPos = getTokenStart() + 4;
                return NULL;
            }
        }
        if (indexOf(' ') != -1 || indexOf('\t') != -1) return QUOTELESS_STRING_TOKEN;
        java.util.regex.Matcher numberMatcher = numberPattern.matcher(zzBuffer);
        if (!numberMatcher.find(getTokenStart()) || numberMatcher.start() != getTokenStart())
            return QUOTELESS_STRING_TOKEN;
        int delta = getTokenEnd() - numberMatcher.end();
        if (delta < 0) return QUOTELESS_STRING_TOKEN;
        zzMarkedPos -= delta;
        return NUMBER_TOKEN;
    }
    public boolean shouldAdvanceQLS() throws IOException {
//        java.util.regex.Matcher numberMatcher = numberPattern.matcher(zzBuffer);
        if (zzMarkedPos >= zzEndRead || zzMarkedPos  >= zzBuffer.length()) return true;
        char nextChar = yycharat(zzMarkedPos - zzStartRead);
        if (nextChar == ',' || nextChar == ']' || nextChar == '}' ||nextChar=='/') {
            if(nextChar!='/') zzMarkedPos += 1;
            while (true) {
                int state = zzState;
                int prevStateStack = stateStack.size;
//            int lastState= prevStateStack ==0?-1:stateStack.peek();

                int markedPos = zzMarkedPos;
                int currentPos = zzCurrentPos;
                int startRead = zzStartRead;
                IElementType nextToken = advanceToken();
                if (!newLine() && nextToken!=LINE_COMMENT_TOKEN || nextToken==null) {
                    zzStartRead = startRead;
                    zzCurrentPos = currentPos;
                    if(nextToken==null)break;
                } else {
                    zzStartRead = startRead;
                    stateStack.size = prevStateStack;
                    zzState = state;
                    zzCurrentPos = currentPos;
                    zzMarkedPos = markedPos;
                    break;
                }
            }
        }
        return true;
    }
    public IElementType QUOTELESS_STRING_TOKEN() throws IOException {
        int state = zzState;
        int lexicalState = zzLexicalState;
        int currentPos = zzCurrentPos;
        int markedPos = zzMarkedPos;
        int startRead = zzStartRead;
//        0b10+0b100
//        zzState = COMMENT_CONTEXT;
        yybegin(COMMENT_CONTEXT);
        zzMarkedPos=zzCurrentPos=startRead;
        IElementType commentToken = advanceToken();
        if (commentToken == BLOCK_COMMENT_TOKEN) {
            zzLexicalState=lexicalState;
//            zzState = state;
            return commentToken;
        }

        zzLexicalState=lexicalState;
        zzStartRead = startRead;
        zzCurrentPos = currentPos;
        zzMarkedPos = markedPos;
        return QUOTELESS_STRING_TOKEN;
    }
    public IElementType QUOTELESS_STRING_TOKEN_OBJECT() throws IOException {
        int slash = indexOf('/');
        if (slash == -1) return QUOTELESS_STRING_TOKEN();
        int star = indexOf('*');
        if (star == -1 || slash+1!=star) return QUOTELESS_STRING_TOKEN();
        Matcher matcher = numberPattern.matcher(zzBuffer);
        if (!matcher.find() || matcher.start()!=getTokenStart() || matcher.end()!=slash) return QUOTELESS_STRING_TOKEN();
        zzMarkedPos=zzCurrentPos=matcher.end();

        return NUMBER_TOKEN;
    }
%}

%state IN_OBJECT
%state IN_ARRAY
%state IN_MEMBER_VALUE
%state COMMENT_CONTEXT


//EOL=\R
WHITE_SPACE=\s+

LINE_COMMENT=("//"|#).*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*(\*+"/")?

DOUBLE_QUOTED_STRING=\"([^\\\"\r\n]|\\[^\r\n])*\"?
SINGLE_QUOTED_STRING='([^\\'\r\n]|\\[^\r\n])*'?
MULTILINE_STRING_TOKEN='''([^']*('{1,2}[^'])*)+(''')?
//JSON_STRING_SPECIAL_LETTER=\\(\"|'|\\|\/|b|f|n|r|t|([uU][0-9a-fA-F]{4}))
NUMBER=-?(0|\d+)(\.\d+)?([Ee][+-]?\d+)?
QUOTELESS_STRING=[^'\"\s,:\[\]{}]+

%%

{LINE_COMMENT}                                { return LINE_COMMENT_TOKEN; }
{BLOCK_COMMENT}                               { return BLOCK_COMMENT_TOKEN; }
{DOUBLE_QUOTED_STRING}                        { return DOUBLE_QUOTED_STRING_TOKEN; }
{SINGLE_QUOTED_STRING}                        { return SINGLE_QUOTED_STRING_TOKEN; }
{MULTILINE_STRING_TOKEN}                      { return MULTILINE_STRING_TOKEN; }
"false"                                       { return FALSE; }
"true"                                        { return TRUE; }
"null"                                        { return NULL; }
<COMMENT_CONTEXT>{
{LINE_COMMENT}                                { return LINE_COMMENT_TOKEN; }
{BLOCK_COMMENT}                               { return BLOCK_COMMENT_TOKEN; }
}
<IN_ARRAY,IN_MEMBER_VALUE>{
    {NUMBER}                                  { return NUMBER_TOKEN; }
    [^\'\" \n,:\[\]{}]([^\n,\]}/]*[^\s,}/\]])?        { if(shouldAdvanceQLS()) return QUOTELESS_STRING_TOKEN(); }
    //-?(0|\d+)(\.\d+)?([Ee][+-]?\d+)?                              { return NUMBER_TOKEN; }
}
<IN_MEMBER_VALUE>{

    {WHITE_SPACE}                             { if(checkQuitFromMemberValue()){popState();}; return WHITE_SPACE; }
    ","                                       { popState(); return COMMA; }
}
<IN_ARRAY>{
    \s+                                       { return WHITE_SPACE; }
    ","                                       { return COMMA; }
}
<IN_OBJECT,IN_ARRAY,IN_MEMBER_VALUE> {
    "{"                                       { nextState(IN_OBJECT); return L_CURLY; }
    "}"                                       { popState(); return R_CURLY; }
    "["                                       { nextState(IN_ARRAY); return L_BRACKET; }
    "]"                                       { popState(); return R_BRACKET; }
    {LINE_COMMENT}                            { return LINE_COMMENT_TOKEN; }
    {BLOCK_COMMENT}                           { return BLOCK_COMMENT_TOKEN; }
}
<IN_OBJECT> {
    {WHITE_SPACE}                             {return WHITE_SPACE; }

    //"'"                         { return SINGLE_QUOTE; }
    //"\""                         { return DOUBLE_QUOTE; }
    ":"                                       {nextState(IN_MEMBER_VALUE);previuseNonEmptyToken=null; return COLON; }


    //{DOUBLE_QUOTED_STRING}                    { return DOUBLE_QUOTED_STRING_TOKEN; }
    //{SINGLE_QUOTED_STRING}                    { return SINGLE_QUOTED_STRING_TOKEN; }
    {NUMBER}                                  { return NUMBER_TOKEN; }
    [^\'\" \n,:\[\]{}]([^\n:]*[^\s:])?        { return QUOTELESS_STRING_TOKEN_OBJECT(); }
    ","                                       { return COMMA; }
}

[^] { return BAD_CHARACTER; }