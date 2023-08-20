package com.zelaux.hjson.editor.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HJsonFoldingBuilder implements FoldingBuilder, DumbAware {
    private static void collectDescriptorsRecursively(@NotNull ASTNode node,
                                                      @NotNull Document document,
                                                      @NotNull List<FoldingDescriptor> descriptors) {
        final IElementType type = node.getElementType();
        if ((type == HJsonElementTypes.OBJECT_FULL || type == HJsonElementTypes.ARRAY) && spanMultipleLines(node, document)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        } else if (type == HJsonElementTypes.BLOCK_COMMENT_TOKEN) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        } else if (type == HJsonElementTypes.MULTILINE_STRING_TOKEN) {
            String nodeText = node.getText();
            int i = nodeText.indexOf('\n');
            if (i != -1) {
                i = nodeText.indexOf('\n', i);
            }
            if (i != -1) descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        } else if (type == HJsonElementTypes.LINE_COMMENT_TOKEN) {
            final Couple<PsiElement> commentRange = expandLineCommentsRange(node.getPsi());
            final int startOffset = commentRange.getFirst().getTextRange().getStartOffset();
            final int endOffset = commentRange.getSecond().getTextRange().getEndOffset();
            if (document.getLineNumber(startOffset) != document.getLineNumber(endOffset)) {
                descriptors.add(new FoldingDescriptor(node, new TextRange(startOffset, endOffset)));
            }
        }

        for (ASTNode child : node.getChildren(null)) {
            collectDescriptorsRecursively(child, document, descriptors);
        }
    }

    @NotNull
    public static Couple<PsiElement> expandLineCommentsRange(@NotNull PsiElement anchor) {
        return Couple.of(HJsonPsiUtil.findFurthestSiblingOfSameType(anchor, false), HJsonPsiUtil.findFurthestSiblingOfSameType(anchor, true));
    }

    private static boolean spanMultipleLines(@NotNull ASTNode node, @NotNull Document document) {
        final TextRange range = node.getTextRange();
        int endOffset = range.getEndOffset();
        return document.getLineNumber(range.getStartOffset())
                < (endOffset < document.getTextLength() ? document.getLineNumber(endOffset) : document.getLineCount() - 1);
    }

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        final List<FoldingDescriptor> descriptors = new ArrayList<>();
        collectDescriptorsRecursively(node, document, descriptors);
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        final IElementType type = node.getElementType();
        if (type == HJsonElementTypes.OBJECT_FULL) {
            final HJsonObject object = node.getPsi(HJsonObjectFull.class);
            final List<HJsonMember> properties = object.getMemberList();
            HJsonMember candidate = null;
            for (HJsonMember property : properties) {
                final String name = property.getName();
                final HJsonValue value = property.getValue();
                if (value instanceof HJsonLiteral) {
                    if ("id".equals(name) || "name".equals(name)) {
                        candidate = property;
                        break;
                    }
                    if (candidate == null) {
                        candidate = property;
                    }
                }
            }
            if (candidate != null) {
                return "{\"" + candidate.getName() + "\": " + candidate.getValue().getText() + "...}";
            }
            return "{...}";
        } else if (type == HJsonElementTypes.ARRAY) {
            HJsonArray array = node.getPsi(HJsonArray.class);
            List<HJsonValue> list = array.getValueList();
            StringBuilder builder = new StringBuilder("[");
            boolean reqDots = list.size() > 1;
            if (list.size() > 0) {
                String text = list.get(0).getText();
                int newLineIndex = text.indexOf('\n');
                reqDots |= newLineIndex != -1;
                if (newLineIndex != -1) {
                    builder.append(text, 0, newLineIndex);
                } else {
                    builder.append(text);
                }
            }
            if (reqDots) {
                builder.append("...");
            }
            builder.append(']');
            return builder.toString();
        } else if (type == HJsonElementTypes.LINE_COMMENT_TOKEN) {
            return "//...";
        } else if (type == HJsonElementTypes.BLOCK_COMMENT_TOKEN) {
            return "/*...*/";
        } else if (type == HJsonElementTypes.MULTILINE_STRING_TOKEN) {
            String nodeText = node.getText();
            LineTokenizer tokenizer = new LineTokenizer(nodeText);
            if (tokenizer.atEnd() || nodeText.matches("'''(''')?")) {
                return "''''''";
            } else if (nodeText.matches("'''\\s+(''')?")) {
                return "'''...'''";
            }
            int startOffset = tokenizer.getOffset();
            if (tokenizer.getLength() <= 3) {
                tokenizer.advance();
                startOffset=tokenizer.getOffset();
            } else {
                startOffset += 3;
            }
            int endOffset = tokenizer.getOffset() + tokenizer.getLength();
            for (int i = 0; i < 3; i++) {
                if (endOffset - i <= startOffset) break;
                if (nodeText.charAt(endOffset - i) != '\'') break;
                if (i == 2) {
                    endOffset -= 3;
                }
            }
            String middleText = nodeText.substring(startOffset, endOffset);
            tokenizer.advance();
            if (!tokenizer.atEnd() && (tokenizer.getOffset() + tokenizer.getLength() < nodeText.length() || !nodeText.endsWith("'''"))) {
                return "'''" + middleText + "...'''";
            }
            return "'''" + middleText + "'''";
        }
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
