package com.zelaux.hjson.inspections;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.psi.HJsonElementVisitor;
import com.zelaux.hjson.psi.HJsonFactory;
import com.zelaux.hjson.psi.HJsonJsonString;
import com.zelaux.hjson.psi.HJsonMember;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HJsonMemberNameValidator extends LocalInspectionTool {
    static final Pattern spacePattern = Pattern.compile(" ");

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new HJsonElementVisitor() {
            @Override
            public void visitMember(@NotNull HJsonMember o) {
                PsiElement memberName = o.getMemberName();
                Matcher matcher = spacePattern.matcher(memberName.getText());
                while (matcher.find()) {
                    holder.registerProblem(memberName, new TextRange(matcher.start(), matcher.end()), "Member name cannot contains spaces",
                            new ToString(true),
                            new ToString(false)
                    );
                }
                super.visitMember(o);
            }
        };
    }

    static class ToString implements LocalQuickFix {
        public final boolean isDouble;
        public final char quote;

        public ToString(boolean isDouble) {
            this.isDouble = isDouble;
            this.quote = isDouble ? '"' : '\'';
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            if (isDouble) {
                return HJsonBundle.message("fix.to-double-quoted-string.name");
            } else {
                return HJsonBundle.message("fix.to-single-quoted-string.name");
            }
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement nameElement = descriptor.getPsiElement();
            String text = nameElement.getText();
            HJsonJsonString newElement = HJsonFactory.getInstance(project).createJsonStringLiteral(quote, text);
            WriteAction.run(() -> {
                nameElement.replace(newElement);
            });
        }
    }
}
