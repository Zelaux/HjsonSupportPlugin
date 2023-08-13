package com.zelaux.hjson.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor;
import com.intellij.util.DocumentUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonObject;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonTrailingCommaRemover implements PreFormatProcessor {
    @NotNull
    @Override
    public TextRange process(@NotNull ASTNode element, @NotNull TextRange range) {
        PsiElement rootPsi = element.getPsi();
        if (rootPsi.getLanguage() != HJsonLanguage.INSTANCE) {
            return range;
        }
        HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(rootPsi.getContainingFile(), HJsonCodeStyleSettings.class);
        if (settings.KEEP_TRAILING_COMMA) {
            return range;
        }
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(rootPsi.getProject());
        Document document = psiDocumentManager.getDocument(rootPsi.getContainingFile());
        if (document == null) {
            return range;
        }
        DocumentUtil.executeInBulk(document, () -> {
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
            PsiElementVisitor visitor = new HJsonTrailingCommaRemover.Visitor(document);
            rootPsi.accept(visitor);
            psiDocumentManager.commitDocument(document);
        });
        return range;
    }

    private static class Visitor extends HJsonRecursiveElementVisitor {
        private final Document myDocument;
        private int myOffsetDelta;

        Visitor(Document document) {
            myDocument = document;
        }

        @Override
        public void visitArray(@NotNull HJsonArray o) {
            super.visitArray(o);
            PsiElement lastChild = o.getLastChild();
            if (lastChild == null || lastChild.getNode().getElementType() != HJsonElementTypes.R_BRACKET) {
                return;
            }
            deleteTrailingCommas(ObjectUtils.coalesce(ContainerUtil.getLastItem(o.getValueList()), o.getFirstChild()));
        }

        @Override
        public void visitObject(@NotNull HJsonObject o) {
            super.visitObject(o);
            PsiElement lastChild = o.getLastChild();
            if (lastChild == null || lastChild.getNode().getElementType() != HJsonElementTypes.R_CURLY) {
                return;
            }
            deleteTrailingCommas(ObjectUtils.coalesce(ContainerUtil.getLastItem(o.getMemberList()), o.getFirstChild()));
        }

        private void deleteTrailingCommas(@Nullable PsiElement lastElementOrOpeningBrace) {
            PsiElement element = lastElementOrOpeningBrace != null ? lastElementOrOpeningBrace.getNextSibling() : null;

            while (element != null) {
                if (element.getNode().getElementType() == HJsonElementTypes.COMMA ||
                    element instanceof PsiErrorElement && ",".equals(element.getText())) {
                    deleteNode(element.getNode());
                }
                else if (!(element instanceof PsiComment || element instanceof PsiWhiteSpace)) {
                    break;
                }
                element = element.getNextSibling();
            }
        }

        private void deleteNode(@NotNull ASTNode node) {
            int length = node.getTextLength();
            myDocument.deleteString(node.getStartOffset() + myOffsetDelta, node.getStartOffset() + length + myOffsetDelta);
            myOffsetDelta -= length;
        }
    }
}
