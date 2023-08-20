package com.zelaux.hjson.codeinsight;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.json.JsonBundle;
import com.intellij.json.codeinsight.JsonLiteralChecker;
import com.intellij.json.codeinsight.StandardJsonLiteralChecker;
import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonMemberNameReference;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HJsonLiteralAnnotator implements Annotator {

    private static class Holder {
        private static final boolean DEBUG = ApplicationManager.getApplication().isUnitTestMode();
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        /*List<HJsonLiteralChecker> extensions = HJsonLiteralChecker.EP_NAME.getExtensionList();*/
        /*if (element instanceof HJsonMemberName) {
            highlightPropertyKey(element, holder);
        }*/
        if (element.getNode().getElementType() == HJsonElementTypes.MEMBER_NAME) {
            String text = element.getText();
            char firstChar = text.charAt(0);
            HJsonPsiUtil.stripQuotes(text);

            if(firstChar=='"' || firstChar=='\''){
                if (firstChar!=text.charAt(text.length()-1)|| HJsonPsiUtil.isEscapedChar(text, text.length() - 1)) {
                    holder.newAnnotation(HighlightSeverity.ERROR,HJsonBundle.message("syntax.error.missing.closing.quote")).create();
                }
            }
        } else if (element instanceof HJsonJsonString) {
            final HJsonStringLiteral stringLiteral = (HJsonStringLiteral) element;
            final int elementOffset = element.getTextOffset();
//            highlightPropertyKey(element, holder);
//            final String text = HJsonPsiUtil.getElementTextWithoutHostEscaping(element);
//            final int length = text.length();

            // Check that string literal is closed properly
            /*if (length <= 1 || text.charAt(0) != text.charAt(length - 1) || HJsonPsiUtil.isEscapedChar(text, length - 1)) {
                holder.newAnnotation(HighlightSeverity.ERROR, HJsonBundle.message("syntax.error.missing.closing.quote")).create();
            }*/

            // Check escapes
            final List<Pair<TextRange, String>> fragments = stringLiteral.getTextFragments();
            for (Pair<TextRange, String> fragment : fragments) {
                HJsonLiteralChecker checker = new HJsonLiteralChecker();

                Pair<TextRange, @InspectionMessage String> error = checker.getErrorForStringFragment(fragment, stringLiteral);
                if (error != null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, error.second).range(error.getFirst().shiftRight(elementOffset)).create();
                }
            }
        } else if (element instanceof HJsonNumberLiteral) {
            HJsonLiteralChecker checker = new HJsonLiteralChecker();
            String text = HJsonPsiUtil.getElementTextWithoutHostEscaping(element);
            String error = checker.getErrorForNumericLiteral(text);
            if (error != null) {
                holder.newAnnotation(HighlightSeverity.ERROR, error).create();
            }

        }
    }

    private static void highlightPropertyKey(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (HJsonPsiUtil.isPropertyKey(element)) {
            if (HJsonLiteralAnnotator.Holder.DEBUG) {
                holder.newAnnotation(HighlightSeverity.INFORMATION, JsonBundle.message("annotation.property.key")).textAttributes(JsonSyntaxHighlighterFactory.JSON_PROPERTY_KEY).create();
            } else {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).textAttributes(JsonSyntaxHighlighterFactory.JSON_PROPERTY_KEY).create();
            }
        }
    }
}
