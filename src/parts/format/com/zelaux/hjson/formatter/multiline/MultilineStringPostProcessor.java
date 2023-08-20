package com.zelaux.hjson.formatter.multiline;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.formatter.comma.HJsonCommaRemoverVisitor;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonFile;
import com.zelaux.hjson.psi.HJsonMultilineString;
import org.jetbrains.annotations.NotNull;

public class MultilineStringPostProcessor implements PostFormatProcessor {
    private static boolean isApplicable(@NotNull PsiElement source) {
        if (source.isValid()) {
            PsiFile file = source.getContainingFile();
            return file instanceof HJsonFile;
        }
        return false;
    }

    @Override
    public @NotNull PsiElement processElement(@NotNull PsiElement source, @NotNull CodeStyleSettings settings) {
        return isApplicable(source) ? new MultilineIndentVisitor(settings).process(source) : source;
    }

    @Override
    public @NotNull TextRange processText(@NotNull PsiFile source, @NotNull TextRange rangeToReformat, @NotNull CodeStyleSettings settings) {
        return isApplicable(source) ? new MultilineIndentVisitor(settings).processText(source, rangeToReformat) : rangeToReformat;
    }
}
