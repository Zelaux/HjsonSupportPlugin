package com.zelaux.hjson.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.psi.HJsonElementVisitor;
import com.zelaux.hjson.psi.HJsonJsonString;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;

public class QuoteChecker extends LocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new HJsonElementVisitor() {
            @Override
            public void visitJsonString(@NotNull HJsonJsonString o) {
                super.visitJsonString(o);
                String value = o.getValue();
                if (o.getText().length() - value.length() < 2) {
                    holder.registerProblem(o, HJsonBundle.message("syntax.error.missing.closing.quote"), new AddLastQuoteFix(String.valueOf(o.getText().charAt(0))));
                }
            }
/*
            @Override
            public void visitMember(@NotNull HJsonMember o) {
                PsiElement memberName = o.getMemberName();
                String text = memberName.getText();
                if (text.charAt(0) == '\"' || text.charAt(0) == '\'') {
                    holder.registerProblem(memberName, HJsonBundle.message("syntax.error.missing.closing.quote"), new AddLastQuoteFix(String.valueOf(memberName.getText().charAt(0))));
                }
                super.visitMember(o);
            }*/
        };
    }

    static class AddLastQuoteFix implements LocalQuickFix {
        public final String closingPart;

        AddLastQuoteFix(String closingPart) {
            this.closingPart = closingPart;
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return HJsonBundle.message("fix.add-closing-quite.family.name");
        }

        @Override
        public @IntentionName @NotNull String getName() {
            return HJsonBundle.message("fix.add-closing-quite.name", closingPart);
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            Document document = PsiDocumentManager.getInstance(project).getDocument(element.getContainingFile());
            if (document != null) WriteAction.run(() -> {
                document.insertString(element.getTextOffset() + element.getTextLength(), closingPart);
            });
        }
    }
}
