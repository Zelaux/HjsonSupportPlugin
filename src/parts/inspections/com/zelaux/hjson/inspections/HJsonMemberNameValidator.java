package com.zelaux.hjson.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HJsonMemberNameValidator extends LocalInspectionTool {
    static final Pattern spacePattern = Pattern.compile(" ");

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new HJsonElementVisitor() {

        };
    }

    /*static class ToString implements LocalQuickFix {
        public final boolean isDouble;
        public final char quote;

        public ToString(boolean isDouble) {
            this.isDouble = isDouble;
            this.quote = isDouble ? '"' : '\'';
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            if(isDouble) {
                return HJsonBundle.message("fix.to-double-quoted-string.name");
            } else{
                return HJsonBundle.message("fix.to-single-quoted-string.name");
            }
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            HJsonQuoteLessString element = (HJsonQuoteLessString) descriptor.getPsiElement();
            String text = element.getValue();
            HJsonJsonString newElement = HJsonFactory.getInstance(project).createJsonStringLiteral(quote, text);
            WriteAction.run(() -> {
                element.replace(newElement);
            });
        }
    }*/
}
