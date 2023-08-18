package com.zelaux.hjson.psi;

import com.intellij.lang.*;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IFileElementType;
import com.zelaux.hjson.HJsonLanguage;
import org.jetbrains.annotations.NotNull;

public class HJsonFileElementType extends IFileElementType {
    public HJsonFileElementType() {
        super(HJsonLanguage.INSTANCE);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        Project project = psi.getProject();
        Language languageForParser = getLanguageForParser(psi);

        PsiBuilder builder = builder(chameleon, project);
        PsiParser parser = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project);
        ASTNode node = parser.parse(this, builder);
        return node.getFirstChildNode();
    }

    @NotNull
    private static PsiBuilder builder(@NotNull ASTNode chameleon, Project project) {
        ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(HJsonLanguage.INSTANCE);//getParserDefinition(lang, chameleon.getElementType());
        return new PsiBuilderImpl(project, parserDefinition, parserDefinition.createLexer(project), chameleon, chameleon.getChars()){

        };
//        return PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, chameleon.getChars());
    }
}
