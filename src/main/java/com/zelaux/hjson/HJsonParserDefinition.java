package com.zelaux.hjson;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson.ast.HJsonASTFactory;
import com.zelaux.hjson.psi.impl.HJsonFileImpl;
import org.jetbrains.annotations.NotNull;

import static com.zelaux.hjson.HJsonElementTypes.*;
import static com.zelaux.hjson.HJsonElementTypes.LINE_COMMENT;

public class HJsonParserDefinition extends HJsonASTFactory implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet STRING_LITERALS = TokenSet.create(QUOTELESS_STRING,MULTILINE_STRING,SINGLE_QUOTED_STRING,DOUBLE_QUOTED_STRING);

    public static final IFileElementType FILE = new IFileElementType(HJsonLanguage.INSTANCE);

    public static final TokenSet HJSON_BRACES = TokenSet.create(L_CURLY, R_CURLY);
    public static final TokenSet HJSON_BRACKETS = TokenSet.create(L_BRACKET, R_BRACKET);
    public static final TokenSet HJSON_CONTAINERS = TokenSet.create(OBJECT_FULL, ARRAY);
    public static final TokenSet HJSON_BOOLEANS = TokenSet.create(TRUE, FALSE);
    public static final TokenSet KEYWORDS = TokenSet.create(TRUE, FALSE, NULL);
    public static final TokenSet HJSON_LITERALS = TokenSet.create(STRING_LITERAL, NUMBER_LITERAL, NULL_LITERAL, TRUE, FALSE);
    public static final TokenSet HJSON_VALUES = TokenSet.orSet(HJSON_CONTAINERS, HJSON_LITERALS);
    public static final TokenSet HJSON_COMMENTARIES = TokenSet.create(BLOCK_COMMENT, LINE_COMMENT);

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new HJsonLexer();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new HJsonParser();
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return HJSON_COMMENTARIES;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        return HJsonElementTypes.Factory.createElement(astNode);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
        return new HJsonFileImpl(fileViewProvider, HJsonLanguage.INSTANCE);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2) {
        return SpaceRequirements.MAY;
    }
}
