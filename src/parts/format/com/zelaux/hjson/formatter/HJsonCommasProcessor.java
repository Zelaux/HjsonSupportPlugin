package com.zelaux.hjson.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor;
import com.intellij.util.DocumentUtil;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HJsonCommasProcessor implements PreFormatProcessor {
    @NotNull
    @Override
    public TextRange process(@NotNull ASTNode element, @NotNull TextRange range) {
        PsiElement rootPsi = element.getPsi();
        if (rootPsi.getLanguage() != HJsonLanguage.INSTANCE) {
            return range;
        }
        HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(rootPsi.getContainingFile(), HJsonCodeStyleSettings.class);
        if (settings.trailingComma() == CommaState.KEEP && settings.commas() == CommaState.KEEP) {
            return range;
        }
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(rootPsi.getProject());
        Document document = psiDocumentManager.getDocument(rootPsi.getContainingFile());
        if (document == null) {
            return range;
        }
        DocumentUtil.executeInBulk(document, () -> {
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
            PsiElementVisitor visitor = new HJsonCommasProcessor.Visitor(document, settings.trailingComma(), settings.commas());
            rootPsi.accept(visitor);
            psiDocumentManager.commitDocument(document);
        });
        return range;
    }

    private static class Visitor extends HJsonRecursiveElementVisitor {
        private final Document myDocument;
        private final CommaState trailingComma;
        private final CommaState commas;
        private int myOffsetDelta;

        public Visitor(Document myDocument, CommaState trailingComma, CommaState commas) {
            this.myDocument = myDocument;
            this.trailingComma = trailingComma;
            this.commas = commas;
        }

        @Override
        public void visitArray(@NotNull HJsonArray o) {
            super.visitArray(o);
            PsiElement lastChild = o.getLastChild();
            if (lastChild == null || lastChild.getNode().getElementType() != HJsonElementTypes.R_BRACKET) {
                return;
            }
            processCommas(o.getValueList());
        }

        @Override
        public void visitObjectFull(@NotNull HJsonObjectFull o) {
            super.visitObjectFull(o);
            PsiElement lastChild = o.getLastChild();
            if (lastChild == null || lastChild.getNode().getElementType() != HJsonElementTypes.R_CURLY) {
                return;
            }
            visitObject(o);
            /*deleteTrailingCommas(ObjectUtils.coalesce(ContainerUtil.getLastItem(o.getMemberList()), o.getFirstChild()));*/
        }

        @Override
        public void visitObject(@NotNull HJsonObject o) {
            super.visitObject(o);
            processCommas(o.getMemberList());
        }

        private void processCommas(List<? extends HJsonElement> list) {
            for (int i = list.size() - 1; i >= 0; i--) {
                HJsonElement member = list.get(i);
                boolean isLast = i == list.size() - 1;
                if (commas == CommaState.REMOVE || commas == CommaState.KEEP && trailingComma == CommaState.REMOVE && isLast) {
                    deleteTrailingCommas(member, isLast);
                }
                if (commas == CommaState.ADD || commas == CommaState.KEEP && trailingComma == CommaState.ADD && isLast) {
                    if (!hasTrailingComma(member, null)) {
                        PsiElement comma = HJsonFactory.getInstance(member.getProject())
                                .createComma();
                        member.getParent().addAfter(comma, member);
                    }
                }
            }
        }

        private void deleteTrailingCommas(@Nullable PsiElement lastElementOrOpeningBrace, boolean last) {
            PsiElement element = lastElementOrOpeningBrace != null ? lastElementOrOpeningBrace.getNextSibling() : null;

            while (element != null) {
                if (element.getNode().getElementType() == HJsonElementTypes.COMMA ||
                        element instanceof PsiErrorElement && ",".equals(element.getText())) {
                    PsiElement next = element.getNextSibling();
                    if (!last && next != null) {
                        if (next instanceof PsiWhiteSpace && !next.getText().contains("\n")) {
                            break;
                        }
                    }
                    deleteNode(element.getNode());
                } else if (!(element instanceof PsiComment || element instanceof PsiWhiteSpace)) {
                    break;
                }
                element = element.getNextSibling();
            }
        }

        private boolean hasTrailingComma(@NotNull PsiElement element, PsiElement until) {
            element = element.getNextSibling();

            while (element != null && element != until) {
                if (element.getNode().getElementType() == HJsonElementTypes.COMMA ||
                        element instanceof PsiErrorElement && ",".equals(element.getText())) {
                    return true;
                } else if (!(element instanceof PsiComment || element instanceof PsiWhiteSpace)) {
                    break;
                }
                element = element.getNextSibling();
            }
            return false;
        }

        private void deleteNode(@NotNull ASTNode node) {
            node.getTreeParent().removeChild(node);
        }
    }
}
