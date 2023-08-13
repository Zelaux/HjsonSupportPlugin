package com.zelaux.hjson.string;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.intellij.psi.StringEscapesTokenTypes.*;

%%

%{
  public _HJsonStringLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _HJsonStringLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

VALID_STRING_ESCAPE_TOKEN=\\(\"|'|\\|\/|b|f|n|r|t|([uU][0-9a-fA-F]{4}))
INVALID_CHARACTER_ESCAPE_TOKEN=\\[^\"'\\/bfnrtuU]
INVALID_UNICODE_ESCAPE_TOKEN=\\[uU]([0-9a-fA-F]){0,3}[^0-9a-fA-F\"\'\\]?


%%
<YYINITIAL> {
  {VALID_STRING_ESCAPE_TOKEN}               { return VALID_STRING_ESCAPE_TOKEN; }

  {INVALID_CHARACTER_ESCAPE_TOKEN}                         { return INVALID_CHARACTER_ESCAPE_TOKEN; }
  {INVALID_UNICODE_ESCAPE_TOKEN}                        { return INVALID_UNICODE_ESCAPE_TOKEN; }
}

[^] { continue; }