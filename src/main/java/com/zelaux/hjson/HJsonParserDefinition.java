package com.zelaux.hjson;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson.ast.HJsonASTFactory;
import com.zelaux.hjson.lexer.HJsonLexer;
import com.zelaux.hjson.psi.HJsonTokens;
import com.zelaux.hjson.psi.impl.HJsonFileImpl;
import org.jetbrains.annotations.NotNull;

public class HJsonParserDefinition extends HJsonASTFactory implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(HJsonLanguage.INSTANCE);

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
        return HJsonTokens.WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return HJsonTokens.comments;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return HJsonTokens.STRING_TOKENS;
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
