package com.zelaux.hjson.editor.selection;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandlerBase;
import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lexer.StringLiteralLexer;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.lexer.HJsonStringLexer;
import com.zelaux.hjson.psi.HJsonStringLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class HJsonStringLiteralSelectionHandler extends ExtendWordSelectionHandlerBase {
    @Override
    public boolean canSelect(@NotNull PsiElement e) {
        if (!(e.getParent() instanceof HJsonStringLiteral)) {
            return false;
        }
        return !InjectedLanguageManager.getInstance(e.getProject()).isInjectedFragment(e.getContainingFile());
    }

    @Override
    public List<TextRange> select(@NotNull PsiElement e, @NotNull CharSequence editorText, int cursorOffset, @NotNull Editor editor) {
        final HJsonStringLexer lexer = new HJsonStringLexer();
        final List<TextRange> result = new ArrayList<>();
        SelectWordUtil.addWordHonoringEscapeSequences(editorText, e.getTextRange(), cursorOffset, lexer, result);

        final PsiElement parent = e.getParent();
        result.add(ElementManipulators.getValueTextRange(parent).shiftRight(parent.getTextOffset()));
        return result;
    }
}
