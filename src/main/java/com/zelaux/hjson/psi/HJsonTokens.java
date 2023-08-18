package com.zelaux.hjson.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;

import static com.zelaux.hjson.HJsonElementTypes.*;

public interface HJsonTokens {
    TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    TokenSet STRING_TOKENS = TokenSet.create(
            QUOTELESS_STRING_TOKEN,
            MULTILINE_STRING_TOKEN,
            SINGLE_QUOTED_STRING_TOKEN,
            DOUBLE_QUOTED_STRING_TOKEN);
    TokenSet HJSON_BRACES = TokenSet.create(L_CURLY, R_CURLY);
    TokenSet HJSON_BRACKETS = TokenSet.create(L_BRACKET, R_BRACKET);
    TokenSet HJSON_CONTAINERS = TokenSet.create(OBJECT_FULL, ARRAY);
    TokenSet HJSON_BOOLEANS = TokenSet.create(TRUE, FALSE);
    TokenSet KEYWORDS = TokenSet.create(TRUE, FALSE, NULL);
    TokenSet HJSON_LITERALS = TokenSet.create(STRING_LITERAL, NUMBER_LITERAL, NULL_LITERAL, TRUE, FALSE);
    TokenSet HJSON_VALUES = TokenSet.orSet(HJSON_CONTAINERS, HJSON_LITERALS);
    TokenSet comments = TokenSet.create(BLOCK_COMMENT_TOKEN, LINE_COMMENT_TOKEN);
}
