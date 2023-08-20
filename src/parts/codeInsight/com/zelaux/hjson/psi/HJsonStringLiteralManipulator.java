package com.zelaux.hjson.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class HJsonStringLiteralManipulator extends AbstractElementManipulator<HJsonStringLiteral> {

    @Override
    public HJsonStringLiteral handleContentChange(@NotNull HJsonStringLiteral element, @NotNull TextRange range, String newContent)
            throws IncorrectOperationException {
        assert new TextRange(0, element.getTextLength()).contains(range);

        final String originalContent = element.getText();
        final TextRange withoutQuotes = getRangeInElement(element);
        final HJsonFactory generator = HJsonFactory.getInstance(element.getProject());
        final String replacement = originalContent.substring(withoutQuotes.getStartOffset(), range.getStartOffset()) +
                newContent +
                originalContent.substring(range.getEndOffset(), withoutQuotes.getEndOffset());
        return (HJsonStringLiteral)element.replace(generator.createJsonStringLiteral(replacement));
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull HJsonStringLiteral element) {
        final String content = element.getText();
        return HJsonPsiUtil.inTextRangeWithoutQuotes(content);
        /*final int startOffset = content.startsWith("'") || content.startsWith("\"") ? 1 : 0;
        final int endOffset = content.length() > 1 && (content.endsWith("'") || content.endsWith("\"")) ? -1 : 0;
        return new TextRange(startOffset, content.length() + endOffset);*/
    }
}
