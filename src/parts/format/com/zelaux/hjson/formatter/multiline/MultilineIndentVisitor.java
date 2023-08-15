package com.zelaux.hjson.formatter.multiline;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessorHelper;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.HJsonFactory;
import com.zelaux.hjson.psi.HJsonMemberValue;
import com.zelaux.hjson.psi.HJsonMultilineString;
import com.zelaux.hjson.psi.HJsonValue;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;

public class MultilineIndentVisitor extends HJsonRecursiveElementVisitor {

    private final PostFormatProcessorHelper myPostProcessor;

    public MultilineIndentVisitor(CodeStyleSettings settings) {
        myPostProcessor = new PostFormatProcessorHelper(settings.getCommonSettings(HJsonLanguage.INSTANCE));
    }

    public PsiElement process(PsiElement formatted) {
        formatted.accept(this);
        return formatted;

    }

    public TextRange processText(final PsiFile source, final TextRange rangeToReformat) {
        myPostProcessor.setResultTextRange(rangeToReformat);
        source.accept(this);
        return myPostProcessor.getResultTextRange();
    }

    protected boolean checkElementOverlapsRange(final PsiElement element) {
        return myPostProcessor.isElementPartlyInRange(element);
    }

    @Override
    public void visitMemberValue(@NotNull HJsonMemberValue o) {
        super.visitMemberValue(o);
        if (!checkElementOverlapsRange(o)) return;
        HJsonValue value = o.getValue();
        if (!(value instanceof HJsonMultilineString)) return;
        PsiElement root = o;
        while (root.getParent() != null && !(root instanceof PsiFile)) {
            root = root.getParent();
        }
        int offset = value.getTextOffset();

        String rootText = root.getText();
        int lineOffset = offset - StringUtil.lastIndexOf(rootText, '\n', 0, offset) - 1;
        String indent = " ".repeat(lineOffset);
        if (myPostProcessor.getSettings().getIndentOptions().USE_TAB_CHARACTER) {
            indent = indent.replace("    ", "\t");
        }
        String[] lines = ((HJsonMultilineString) value).getValue().split("\n");
        StringBuilder builder = new StringBuilder("'''\n");
        for (String line : lines) {
            builder.append(indent).append(line).append("\n");
        }
        builder.append(indent).append("'''");
        o.replace(HJsonFactory.getInstance(o.getProject()).createValue(builder.toString()));


    }
}

