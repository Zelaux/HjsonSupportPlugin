package com.zelaux.hjson;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = {
            new BracePair(HJsonElementTypes.L_BRACKET, HJsonElementTypes.R_BRACKET, true),
            new BracePair(HJsonElementTypes.L_CURLY, HJsonElementTypes.R_CURLY, true)
    };

    @Override
    public BracePair @NotNull [] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
