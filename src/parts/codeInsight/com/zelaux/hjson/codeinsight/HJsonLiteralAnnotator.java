package com.zelaux.hjson.codeinsight;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.json.JsonBundle;
import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.psi.HJsonMemberName;
import com.zelaux.hjson.psi.HJsonNumberLiteral;
import com.zelaux.hjson.psi.HJsonPsiUtil;
import com.zelaux.hjson.psi.HJsonStringLiteral;
import com.zelaux.hjson.psi.impl.HJsonMemberNameReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HJsonLiteralAnnotator  implements Annotator {

    private static class Holder {
        private static final boolean DEBUG = ApplicationManager.getApplication().isUnitTestMode();
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        /*List<HJsonLiteralChecker> extensions = HJsonLiteralChecker.EP_NAME.getExtensionList();*/
        if (element instanceof HJsonMemberName) {
            highlightPropertyKey(element, holder);
        }
        /*else if (element instanceof HJsonStringLiteral) {
            final HJsonStringLiteral stringLiteral = (HJsonStringLiteral)element;
            final int elementOffset = element.getTextOffset();
            highlightPropertyKey(element, holder);
            final String text = HJsonPsiUtil.getElementTextWithoutHostEscaping(element);
            final int length = text.length();

            // Check that string literal is closed properly
            if (length <= 1 || text.charAt(0) != text.charAt(length - 1) || HJsonPsiUtil.isEscapedChar(text, length - 1)) {
                holder.newAnnotation(HighlightSeverity.ERROR, HJsonBundle.message("syntax.error.missing.closing.quote")).create();
            }

            // Check escapes
            final List<Pair<TextRange, String>> fragments = stringLiteral.getTextFragments();
            for (Pair<TextRange, String> fragment: fragments) {
                for (HJsonLiteralChecker checker: extensions) {
                    if (!checker.isApplicable(element)) continue;
                    Pair<TextRange, @InspectionMessage String> error = checker.getErrorForStringFragment(fragment, stringLiteral);
                    if (error != null) {
                        holder.newAnnotation(HighlightSeverity.ERROR, error.second).range(error.getFirst().shiftRight(elementOffset)).create();
                    }
                }
            }
        }
        else if (element instanceof HJsonNumberLiteral) {
            String text = null;
            for (HJsonLiteralChecker checker: extensions) {
                if (!checker.isApplicable(element)) continue;
                if (text == null) {
                    text = HJsonPsiUtil.getElementTextWithoutHostEscaping(element);
                }
                String error = checker.getErrorForNumericLiteral(text);
                if (error != null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, error).create();
                }
            }
        }*/
    }

    private static void highlightPropertyKey(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (HJsonPsiUtil.isPropertyKey(element)) {
            if (HJsonLiteralAnnotator.Holder.DEBUG) {
                holder.newAnnotation(HighlightSeverity.INFORMATION, JsonBundle.message("annotation.property.key")).textAttributes(JsonSyntaxHighlighterFactory.JSON_PROPERTY_KEY).create();
            }
            else {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION).textAttributes(JsonSyntaxHighlighterFactory.JSON_PROPERTY_KEY).create();
            }
        }
    }
}
