package com.zelaux.hjson;

import com.intellij.codeInsight.editorActions.MultiCharQuoteHandler;
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson.psi.HJsonStringLiteral;
import com.zelaux.hjson.psi.HJsonTokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonQuoteHandler extends SimpleTokenSetQuoteHandler implements MultiCharQuoteHandler {
    public HJsonQuoteHandler() {
        super(TokenSet.andNot(HJsonTokens.STRING_TOKENS, TokenSet.create(HJsonElementTypes.QUOTELESS_STRING_TOKEN)));
    }

    @Nullable
    @Override
    public CharSequence getClosingQuote(@NotNull HighlighterIterator iterator, int offset) {
        final IElementType tokenType = iterator.getTokenType();
        if (tokenType == TokenType.WHITE_SPACE) {
            final int index = iterator.getStart() - 1;
            if (index >= 0) {
                return String.valueOf(iterator.getDocument().getCharsSequence().charAt(index));
            }
        }//DONT WORK FOR '''

        return tokenType == HJsonElementTypes.SINGLE_QUOTED_STRING_TOKEN ? "'" : (tokenType == HJsonElementTypes.DOUBLE_QUOTED_STRING_TOKEN ? "\"" : "'''");
    }

    @Override
    public void insertClosingQuote(@NotNull Editor editor, int offset, @NotNull PsiFile file, @NotNull CharSequence closingQuote) {
        PsiElement element = file.findElementAt(offset - 1);
        PsiElement parent = element == null ? null : element.getParent();
        if (parent instanceof HJsonStringLiteral) {
            PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
            TextRange range = parent.getTextRange();
            if (offset - 1 != range.getStartOffset() || !"\"".contentEquals(closingQuote)) {
                int endOffset = range.getEndOffset();
                if (offset < endOffset) return;
                if (offset == endOffset && !StringUtil.isEmpty(((HJsonStringLiteral) parent).getValue())) return;
            }
        }
        editor.getDocument().insertString(offset, closingQuote);
//        HJsonTypedHandler.processPairedBracesComma(closingQuote.charAt(0), editor, file);
    }
}