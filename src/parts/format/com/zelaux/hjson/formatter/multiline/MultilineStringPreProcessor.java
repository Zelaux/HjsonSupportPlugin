package com.zelaux.hjson.formatter.multiline;

import com.intellij.application.options.CodeStyle;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.PreFormatProcessor;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.util.DocumentUtil;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultilineStringPreProcessor implements PreFormatProcessor {


    @NotNull
    @Override
    public TextRange process(@NotNull ASTNode element, @NotNull TextRange range) {
        PsiElement rootPsi = element.getPsi();
        if (rootPsi.getLanguage() != HJsonLanguage.INSTANCE) {
            return range;
        }
        HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(rootPsi.getContainingFile(), HJsonCodeStyleSettings.class);

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(rootPsi.getProject());
        Document document = psiDocumentManager.getDocument(rootPsi.getContainingFile());
        if (document == null) {
            return range;
        }
        DocumentUtil.executeInBulk(document, () -> {
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
            PsiElementVisitor visitor = new MultilineStringPreProcessor.Visitor(document, settings.trailingComma(), settings.commas());
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
        public void visitMultilineString(@NotNull HJsonMultilineString o) {
            PsiElement stringToken = o.getMultilineStringToken();
            int indent = HJsonPsiImplUtils.getIndent(o);
            StringBuilder builder = new StringBuilder("'''\n");
            String indentValue = " ".repeat(indent);
            for (String line : o.getLines()) {
                builder.append(indentValue).append(line).append("\n");
            }
            builder.append(indentValue).append("'''");
            ((LeafElement)stringToken.getNode()).replaceWithText(builder.toString());
            super.visitMultilineString(o);
        }

    }
}
