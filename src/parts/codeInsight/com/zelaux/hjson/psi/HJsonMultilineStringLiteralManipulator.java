package com.zelaux.hjson.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import com.zelaux.hjson.psi.impl.HJsonStringLiteralImpl;
import com.zelaux.hjson.psi.impl.HJsonStringLiteralMixin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonMultilineStringLiteralManipulator extends AbstractElementManipulator<HJsonMultilineString> {


    @Override
    public @Nullable HJsonMultilineString handleContentChange(@NotNull HJsonMultilineString element, @NotNull TextRange range, String rawContent) throws IncorrectOperationException {

        assert new TextRange(0, element.getTextLength()).contains(range);
        if(range.getLength() ==rawContent.length()){
            if(StringUtil.startsWith(element.getText(),range.getStartOffset(),rawContent))return element;
        }
        LineTokenizer tokenizer = new LineTokenizer(rawContent);
        StringBuilder newContent = new StringBuilder();
        int indent = element.getIndent();
        while (!tokenizer.atEnd()) {
            if (newContent.length() > 0) {
                appendIndent(newContent, indent);
            }
            newContent.append(rawContent, tokenizer.getOffset(), tokenizer.getOffset() + tokenizer.getLength());
            tokenizer.advance();
        }
        if (tokenizer.getLineSeparatorLength() > 0) appendIndent(newContent, indent);
        ((HJsonStringLiteralMixin) element).updateText(range.replace(element.getText(), newContent.toString()));
        return element;
    }

    private static void appendIndent(StringBuilder newContent, int indent) {
        newContent.append("\n");
        newContent.append(" ".repeat(indent));
    }

    @Override
    public @NotNull TextRange getRangeInElement(@NotNull HJsonMultilineString element) {
        String text = element.getText();
        if (text.length() > 6 && text.endsWith("'''")) return new TextRange(3, text.length() - 3);
        return new TextRange(3, text.length());
    }
}
