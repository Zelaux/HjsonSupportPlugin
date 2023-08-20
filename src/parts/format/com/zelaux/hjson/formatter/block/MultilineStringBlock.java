package com.zelaux.hjson.formatter.block;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.psi.PsiElement;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import static com.zelaux.hjson.psi.impl.HJsonPsiImplUtils.nonSpacePattern;

public class MultilineStringBlock extends HJsonBlock {
//    public static final Key<String[]> RAW_LINES =Key.create("HJSON_MULTI_LINE_RAW");

    public MultilineStringBlock(@Nullable HJsonBlock parent,
                                @NotNull ASTNode node,
                                @NotNull HJsonCodeStyleSettings customSettings,
                                @Nullable Alignment alignment,
                                @NotNull Indent indent,
                                @Nullable Wrap wrap,
                                @NotNull SpacingBuilder spacingBuilder) {
        super(parent, node, customSettings, alignment, indent, wrap, spacingBuilder);
    }

    @Override
    public @NotNull List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            int offset = myNode.getStartOffset();
            boolean isArrayElement = myNode.getTreeParent().getElementType() == HJsonElementTypes.ARRAY;
            Alignment alignment =isArrayElement ? myAlignment : Alignment.createAlignment(false, Alignment.Anchor.LEFT);
            List<TextRange> textRanges = extractLinesRanges();
            List<Block> children = new ArrayList<>(textRanges.size());
            if(isArrayElement && false){
                collectChildren(Alignment.createChildAlignment(myAlignment), 0, textRanges, children );
            } else {
                for (int i = 0; i < textRanges.size(); i++) {
                    TextRange range = textRanges.get(i).shiftRight(offset);
//                System.out.println("|"+range.substring(text)+"|");
                    Indent indent = i == 0 ? Indent.getNoneIndent() :
                            Indent.getContinuationIndent();
                    children.add(new TextLineBlock(range,  alignment,Indent.getNormalIndent() , null, Collections.emptyList()));
                }
            }
//            String text = myNode.getPsi().getContainingFile().getText();
            return mySubBlocks = children;
        }
        return mySubBlocks;
    }

    private void collectChildren(Alignment alignment, int offset, List<TextRange> ranges, List<Block> children) {
        List<Block> list=Collections.emptyList();
        if(offset+1<children.size()){
            list=new ArrayList<>();
            collectChildren(alignment, offset+1, ranges, children );
        }
        children.add(new TextLineBlock(
                new TextRange(ranges.get(offset).getStartOffset(),ranges.get(ranges.size()-1).getEndOffset())
                        .shiftRight(myNode.getStartOffset())
                ,  alignment,offset==0?Indent.getContinuationIndent():Indent.getNoneIndent(), null,list));
    }

/* @Override
    public @NotNull List<Block> getSubBlocks() {
        if (mySubBlocks != null) return mySubBlocks;
        int offset = this.myNode.getStartOffset();
        Alignment alignment = Alignment.createAlignment();;
//        Alignment child = Alignment.createChildAlignment(alignment);
        List<TextRange> textRanges = extractLinesRanges();
        List<Block> children = new ArrayList<>(textRanges.size());
//        List<Block> subChildren = new ArrayList<>(textRanges.size() - 1);
//        Wrap  wrap=Wrap.createWrap(2,false);
        Wrap wrap = null;//Wrap.createWrap(WrapType.NONE, false);
        {

        }
        for (int i = 0; i < textRanges.size(); i++) {

            *//*Indent indent = myNode.getTreeParent().getElementType() != HJsonElementTypes.MEMBER_VALUE ? Indent.getNoneIndent() :
                    Indent.getContinuationIndent();*//*
            Indent indent = i==0 ? Indent.getNoneIndent() :
                    Indent.getContinuationIndent();
            TextRange range = textRanges.get(i).shiftRight(offset);
            TextLineBlock block = new TextLineBlock(range,
                    null,null,
                    null);
//            children.add(block);
//             wrap = Wrap.createChildWrap(wrap,WrapType.ALWAYS, true);
        }
        return mySubBlocks = children;
    }*/

    //    private Alignment childAlignment = Alignment.createAlignment();
    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        System.out.println("TOTOTOTOTOOTOTOTOTOT "+newChildIndex);
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @NotNull
    private List<TextRange> extractLinesRanges() {
        PsiElement psi = myNode.getPsi();
        String myText = psi.getText();
        int lineIndent = HJsonPsiImplUtils.getIndent(psi);
        LineTokenizer lineTokenizer = new LineTokenizer(myText);
        ArrayList<TextRange> ranges = new ArrayList<>();

        while (!lineTokenizer.atEnd()) {
            int lineStart = lineTokenizer.getOffset();
            int lineLength = lineTokenizer.getLength();
            int lineEnd = lineStart + lineLength;
            int lineSeparatorLength = lineTokenizer.getLineSeparatorLength();

            handle:
            {
                int startIndex;

                int endIndex = lineEnd;
                if (lineStart == 0) {
                    startIndex = 0;
//                    startIndex = 3;
                } else {
                    Matcher matcher = nonSpacePattern.matcher(myText);
                    int foundIndex = lineEnd - 1;
                    if (matcher.find(lineStart) && matcher.end() <= lineEnd) {
                        foundIndex = matcher.start();
                    } else if (lineLength == 0) {
                        foundIndex = lineStart;
                    }
                    startIndex = Math.min(lineIndent + lineStart, foundIndex);
                }
                boolean isLast = lineEnd == myText.length();
                if (isLast && myText.endsWith("'''")) {
//                    endIndex -= 3;
                } else {
//                    endIndex += lineSeparatorLength;
                }
                ranges.add(new TextRange(startIndex, endIndex));
            }
            lineTokenizer.advance();
        }
        return ranges;
    }


    @Override
    public boolean isLeaf() {
        return false;
    }
}
