package com.zelaux.hjson.formatter;

import com.zelaux.hjson.HJsonElementTypes;
import com.intellij.openapi.editor.DefaultLineWrapPositionStrategy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LineWrapPositionStrategy;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonLineWrapPositionStrategy extends DefaultLineWrapPositionStrategy {
    @Override
    public int calculateWrapPosition(@NotNull Document document,
                                     @Nullable Project project,
                                     int startOffset,
                                     int endOffset,
                                     int maxPreferredOffset,
                                     boolean allowToBeyondMaxPreferredOffset,
                                     boolean isSoftWrap) {
        if (isSoftWrap) {
            return super.calculateWrapPosition(document, project, startOffset, endOffset, maxPreferredOffset, allowToBeyondMaxPreferredOffset,
                    true);
        }
        if (project == null) return -1;
        final int wrapPosition = getMinWrapPosition(document, project, maxPreferredOffset);
        if (wrapPosition == SKIP_WRAPPING) return -1;
        int minWrapPosition = Math.max(startOffset, wrapPosition);
        return super
                .calculateWrapPosition(document, project, minWrapPosition, endOffset, maxPreferredOffset, allowToBeyondMaxPreferredOffset, isSoftWrap);
    }

    private static final int SKIP_WRAPPING = -2;
    private static int getMinWrapPosition(@NotNull Document document, @NotNull Project project, int offset) {
        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        if (manager.isUncommited(document)) manager.commitDocument(document);
        PsiFile psiFile = manager.getPsiFile(document);
        if (psiFile != null) {
            PsiElement currElement = psiFile.findElementAt(offset);
            final IElementType elementType = PsiUtilCore.getElementType(currElement);
            if (elementType == HJsonElementTypes.DOUBLE_QUOTED_STRING
                    || elementType == HJsonElementTypes.SINGLE_QUOTED_STRING
                    || elementType == HJsonElementTypes.QUOTELESS_STRING
                    || elementType == HJsonElementTypes.LITERAL
                    || elementType == HJsonElementTypes.BOOLEAN_LITERAL
                    || elementType == HJsonElementTypes.TRUE
                    || elementType == HJsonElementTypes.FALSE
                    || elementType == HJsonElementTypes.NULL_LITERAL
                    || elementType == HJsonElementTypes.NUMBER_LITERAL) {
                return currElement.getTextRange().getEndOffset();
            }
            if (elementType == HJsonElementTypes.COLON) {
                return SKIP_WRAPPING;
            }
            if (currElement != null) {
                if (currElement instanceof PsiComment ||
                        PsiUtilCore.getElementType(PsiTreeUtil.skipWhitespacesForward(currElement)) == HJsonElementTypes.COMMA) {
                    return SKIP_WRAPPING;
                }
            }
        }
        return -1;
    }
}
