package com.zelaux.hjson.editor.lineMover;

import com.intellij.codeInsight.editorActions.moveUpDown.LineMover;
import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonLineMover extends LineMover {
    private enum Direction {
        Same,
        Inside,
        Outside
    }

    private HJsonLineMover.Direction myDirection = HJsonLineMover.Direction.Same;

    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
        myDirection = HJsonLineMover.Direction.Same;

        if (!(file instanceof HJsonFile) || !super.checkAvailable(editor, file, info, down)) {
            return false;
        }

        Pair<PsiElement, PsiElement> movedElementRange = getElementRange(editor, file, info.toMove);
        if (!isValidElementRange(movedElementRange)) {
            return false;
        }

        // Tweak range to move if it's necessary
        movedElementRange = expandCommentsInRange(movedElementRange);

        PsiElement movedSecond = movedElementRange.getSecond();
        PsiElement movedFirst = movedElementRange.getFirst();

        info.toMove = new LineRange(movedFirst, movedSecond);

        // Adjust destination range to prevent illegal offsets
        final int lineCount = editor.getDocument().getLineCount();
        if (down) {
            info.toMove2 = new LineRange(info.toMove.endLine, Math.min(info.toMove.endLine + 1, lineCount));
        }
        else {
            info.toMove2 = new LineRange(Math.max(info.toMove.startLine - 1, 0), info.toMove.startLine);
        }

        if (movedFirst instanceof PsiComment && movedSecond instanceof PsiComment) {
            return true;
        }

        // Check whether additional comma is needed
        final Pair<PsiElement, PsiElement> destElementRange = getElementRange(editor, file, info.toMove2);

        if (destElementRange != null) {
            PsiElement destFirst = destElementRange.getFirst();
            PsiElement destSecond = destElementRange.getSecond();

            if (destFirst == destSecond && !(destFirst instanceof HJsonMember) && !(destFirst instanceof HJsonValue)) {
                PsiElement parent = destFirst.getParent();
                if (((HJsonFile)parent.getContainingFile()).getTopLevelValue() == parent) {
                    info.prohibitMove();
                    return true;
                }
            }

            PsiElement firstParent = destFirst.getParent();
            PsiElement secondParent = destSecond.getParent();

            HJsonValue firstParentParent = PsiTreeUtil.getParentOfType(firstParent, HJsonObject.class, HJsonArray.class);
            if (firstParentParent == secondParent) {
                myDirection = down ? HJsonLineMover.Direction.Outside : HJsonLineMover.Direction.Inside;
            }
            HJsonValue secondParentParent = PsiTreeUtil.getParentOfType(secondParent, HJsonObject.class, HJsonArray.class);
            if (firstParent == secondParentParent) {
                myDirection = down ? HJsonLineMover.Direction.Inside : HJsonLineMover.Direction.Outside;
            }
        }
        return true;
    }

    @NotNull
    private static Pair<PsiElement, PsiElement> expandCommentsInRange(@NotNull Pair<PsiElement, PsiElement> range) {
        final PsiElement upper = HJsonPsiUtil.findFurthestSiblingOfSameType(range.getFirst(), false);
        final PsiElement lower = HJsonPsiUtil.findFurthestSiblingOfSameType(range.getSecond(), true);
        return Pair.create(upper, lower);
    }

    @Override
    public void afterMove(@NotNull Editor editor, @NotNull PsiFile file, @NotNull MoveInfo info, boolean down) {
        int diff = (info.toMove.endLine - info.toMove.startLine) - (info.toMove2.endLine - info.toMove2.startLine);
        switch (myDirection) {
            case Same:
                addCommaIfNeeded(editor.getDocument(), down ? info.toMove.endLine - 1 - diff : info.toMove2.endLine - 1 + diff);
                trimCommaIfNeeded(editor.getDocument(), file, down ? info.toMove.endLine : info.toMove2.endLine + diff);
                break;
            case Inside:
                if (!down) {
                    addCommaIfNeeded(editor.getDocument(), info.toMove2.startLine - 1);
                }
                trimCommaIfNeeded(editor.getDocument(), file, down ? info.toMove.startLine : info.toMove2.startLine);
                trimCommaIfNeeded(editor.getDocument(), file, down ? info.toMove.endLine : info.toMove2.endLine + diff);
                break;
            case Outside:
                addCommaIfNeeded(editor.getDocument(), down ? info.toMove.startLine : info.toMove2.startLine);
                trimCommaIfNeeded(editor.getDocument(), file, down ? info.toMove.endLine : info.toMove2.endLine + diff);
                if (down) {
                    trimCommaIfNeeded(editor.getDocument(), file, info.toMove.startLine - 1);
                    addCommaIfNeeded(editor.getDocument(), info.toMove.endLine);
                    trimCommaIfNeeded(editor.getDocument(), file, info.toMove.endLine);
                }
                break;
        }
    }

    private static int getForwardLineNumber(Document document, PsiElement element) {
        while (element instanceof PsiWhiteSpace || element instanceof PsiComment) {
            element = element.getNextSibling();
        }
        if (element == null) return -1;

        TextRange range = element.getTextRange();
        return document.getLineNumber(range.getEndOffset());
    }

    private static int getBackwardLineNumber(Document document, PsiElement element) {
        while (element instanceof PsiWhiteSpace || element instanceof PsiComment) {
            element = element.getPrevSibling();
        }
        if (element == null) return -1;

        TextRange range = element.getTextRange();
        return document.getLineNumber(range.getEndOffset());
    }

    private static void trimCommaIfNeeded(Document document, PsiFile file, int line) {
        int offset = document.getLineEndOffset(line);
        if (doTrimComma(document, offset + 1, offset)) return;

        PsiElement element = file.findElementAt(offset - 1);
        int forward = getForwardLineNumber(document, element);
        int backward = getBackwardLineNumber(document, element);
        if (forward < 0 || backward < 0) return;
        doTrimComma(document, document.getLineEndOffset(forward) - 1, document.getLineEndOffset(backward));
    }

    private static boolean doTrimComma(Document document, int forwardOffset, int backwardOffset) {
        CharSequence charSequence = document.getCharsSequence();
        if (backwardOffset <= 0) return true;
        if (charSequence.charAt(backwardOffset - 1) == ',') {
            int offsetAfter = skipWhitespaces(charSequence, forwardOffset);
            if (offsetAfter >= charSequence.length()) return true;
            char ch = charSequence.charAt(offsetAfter);

            if (ch == ']' || ch == '}') {
                document.deleteString(backwardOffset - 1, backwardOffset);
            }
            if (ch != '/') return true;
        }
        return false;
    }

    private static int skipWhitespaces(CharSequence charSequence, int offset2) {
        while (offset2 < charSequence.length() && Character.isWhitespace(charSequence.charAt(offset2))) {
            offset2++;
        }
        return offset2;
    }

    private static void addCommaIfNeeded(Document document, int line) {
        int offset = document.getLineEndOffset(line);
        if (offset > 0 && document.getCharsSequence().charAt(offset - 1) != ',') {
            document.insertString(offset, ",");
        }
    }

    private static boolean isValidElementRange(@Nullable Pair<PsiElement, PsiElement> elementRange) {
        if (elementRange == null) {
            return false;
        }
        return elementRange.getFirst().getParent() == elementRange.getSecond().getParent();
    }
}
