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

//EOL=\R
WHITE_SPACE=[\s]+

LINE_COMMENT=("//"|#).*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*(\*+"/")?

DOUBLE_QUOTED_STRING=\"([^\\\"\r\n]|\\[^\r\n])*\"?
SINGLE_QUOTED_STRING='([^\\'\r\n]|\\[^\r\n])*'?
MULTILINE_STRING_TOKEN='''([^']*('{1,2}[^'])*)+(''')?
//JSON_STRING_SPECIAL_LETTER=\\(\"|'|\\|\/|b|f|n|r|t|([uU][0-9a-fA-F]{4}))
NUMBER=-?(0|\d+)(\.\d+)?([Ee][+-]?\d+)?
QUOTELESS_STRING=[^'\"\s,:\[\]{}][^,\s:]*

%%
<YYINITIAL> {
  {WHITE_SPACE}               { return WHITE_SPACE; }

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
   {DOUBLE_QUOTED_STRING}                     { return DOUBLE_QUOTED_STRING_TOKEN; }
   {SINGLE_QUOTED_STRING}                     { return SINGLE_QUOTED_STRING_TOKEN; }
   {MULTILINE_STRING_TOKEN}                     { return MULTILINE_STRING_TOKEN; }

  {LINE_COMMENT}              { return LINE_COMMENT_TOKEN; }
  {BLOCK_COMMENT}             { return BLOCK_COMMENT_TOKEN; }
  {NUMBER}                    { return NUMBER_TOKEN; }

  {QUOTELESS_STRING}                     { return QUOTELESS_STRING_TOKEN; }
}

[^] { return BAD_CHARACTER; }