package com.zelaux.hjson.formatter.comma;

import com.intellij.lang.ASTNode;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessorHelper;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonElement;
import com.zelaux.hjson.psi.HJsonObject;
import com.zelaux.hjson.psi.HJsonObjectFull;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HJsonCommaRemoverVisitor extends HJsonRecursiveElementVisitor {

    private final CommaState trailingComma;
    private final CommaState commas;

    private final PostFormatProcessorHelper myPostProcessor;

    public HJsonCommaRemoverVisitor(CodeStyleSettings settings) {
        myPostProcessor = new PostFormatProcessorHelper(settings.getCommonSettings(JavaLanguage.INSTANCE));
        HJsonCodeStyleSettings customSettings = settings.getCustomSettings(HJsonCodeStyleSettings.class);
        this.commas = customSettings.commas();
        this.trailingComma = customSettings.trailingComma();
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
            if (!checkElementOverlapsRange(member)) continue;
            if (commas == CommaState.REMOVE || commas == CommaState.KEEP && trailingComma == CommaState.REMOVE && isLast) {
                deleteTrailingCommas(member, isLast);
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

