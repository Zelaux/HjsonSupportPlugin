package com.zelaux.hjson;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.zelaux.hjson.HJsonElementTypes.*;

%%

%{
  public _HJsonLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _HJsonLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=[\s]+

LINE_COMMENT=("//"|#).*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*(\*+"/")?

DOUBLE_QUOTED_STRING=\"([^\\\"\r\n]|\\[^\r\n])*\"?
SINGLE_QUOTED_STRING='([^\\'\r\n]|\\[^\r\n])*'?
MULTILINE_STRING_TOKEN='''([^']*('{1,2}[^'])*)+(''')?
//JSON_STRING_SPECIAL_LETTER=\\(\"|'|\\|\/|b|f|n|r|t|([uU][0-9a-fA-F]{4}))
NUMBER=-?(0|\d+)(\.\d+)?([Ee][+-]?\d+)?
QUOTELESS_STRING=([^'\"\s,:\[\]{}\-\d]|((-?(0|\d+)(\.\d+)?([Ee][+-]?\d+)?)[ \t]))[^\n:]*[^\s:]
NEW_LINE=\s*\n\s*

%%
<YYINITIAL> {
  {WHITE_SPACE}               { return WHITE_SPACE; }

  //{NEW_LINE}                         { return NEW_LINE; }
  "{"                         { return L_CURLY; }
  "}"                         { return R_CURLY; }
  "'"                         { return SINGLE_QUOTE; }
  "\""                         { return DOUBLE_QUOTE; }
  "["                         { return L_BRACKET; }
  "]"                         { return R_BRACKET; }
  ","                         { return COMMA; }
  ":"                         { return COLON; }
  "true"                      { return TRUE; }
  "false"                     { return FALSE; }
  "null"                      { return NULL; }
   {QUOTELESS_STRING}                     { return QUOTELESS_STRING; }
   {DOUBLE_QUOTED_STRING}                     { return DOUBLE_QUOTED_STRING; }
   {SINGLE_QUOTED_STRING}                     { return SINGLE_QUOTED_STRING; }
   {MULTILINE_STRING_TOKEN}                     { return MULTILINE_STRING_TOKEN; }

  {LINE_COMMENT}              { return LINE_COMMENT; }
  {BLOCK_COMMENT}             { return BLOCK_COMMENT; }
  //{JSON_STRING_SPECIAL_LETTER} {return JSON_STRING_SPECIAL_LETTER; }
  {NUMBER}                    { return NUMBER; }

}

[^] { return BAD_CHARACTER; }