package com.zelaux.hjson.formatter.comma;

import com.intellij.application.options.CodeStyle;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.BraceEnforcer;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor;
import com.intellij.util.DocumentUtil;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.psi.HJsonFile;
import org.jetbrains.annotations.NotNull;

public class HJsonCommaRemoverProcessor implements PostFormatProcessor {
    private static boolean isApplicable(@NotNull PsiElement source) {
        if (source.isValid()) {
            PsiFile file = source.getContainingFile();
            HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(source.getContainingFile(), HJsonCodeStyleSettings.class);
            if (settings.trailingComma() == CommaState.KEEP && settings.commas() == CommaState.KEEP) {
                return false;
            }
            return file instanceof HJsonFile;
        }
        return false;
    }

    @Override
    public @NotNull PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
        return isApplicable(source) ? new HJsonCommaRemoverVisitor(settings).process(source) : source;
    }

    @Override
    public @NotNull TextRange processText(@NotNull PsiFile source, @NotNull TextRange rangeToReformat, @NotNull CodeStyleSettings settings) {
        return isApplicable(source) ? new HJsonCommaRemoverVisitor(settings).processText(source, rangeToReformat) : rangeToReformat;
    }
}