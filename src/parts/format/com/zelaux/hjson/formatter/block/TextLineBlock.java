package com.zelaux.hjson.formatter.block;

import com.intellij.formatting.*;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TextLineBlock(
        TextRange textRange,
        Alignment alignment,
        Indent indent,
        Spacing spacing,
        List<Block> subBlock
) implements Block {

    @NotNull
    @Override
    public TextRange getTextRange() {
        return textRange;
    }

    @Nullable
    @Override
    public Indent getIndent() {
        return indent;
    }

    @Nullable
    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public @NotNull List<Block> getSubBlocks() {
        return subBlock;
    }


    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(null, null);
    }


    @Override
    public @Nullable Wrap getWrap() {
        return null;
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacing;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
