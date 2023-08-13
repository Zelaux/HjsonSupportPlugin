package com.zelaux.hjson.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonObject;
import com.zelaux.hjson.psi.HJsonPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.zelaux.hjson.HJsonElementTypes.*;
import static com.zelaux.hjson.HJsonElementTypes.MEMBER;
import static com.zelaux.hjson.HJsonParserDefinition.HJSON_CONTAINERS;
import static com.zelaux.hjson.formatter.HJsonCodeStyleSettings.ALIGN_PROPERTY_ON_COLON;
import static com.zelaux.hjson.formatter.HJsonCodeStyleSettings.ALIGN_PROPERTY_ON_VALUE;
import static com.zelaux.hjson.psi.HJsonPsiUtil.hasElementType;

public class HJsonBlock  implements ASTBlock {
    private static final TokenSet JSON_OPEN_BRACES = TokenSet.create(L_BRACKET, L_CURLY);
    private static final TokenSet JSON_CLOSE_BRACES = TokenSet.create(R_BRACKET, R_CURLY);
    private static final TokenSet JSON_ALL_BRACES = TokenSet.orSet(JSON_OPEN_BRACES, JSON_CLOSE_BRACES);

    private final HJsonBlock myParent;

    private final ASTNode myNode;
    private final PsiElement myPsiElement;
    private final Alignment myAlignment;
    private final Indent myIndent;
    private final Wrap myWrap;
    private final HJsonCodeStyleSettings myCustomSettings;
    private final SpacingBuilder mySpacingBuilder;
    // lazy initialized on first call to #getSubBlocks()
    private List<Block> mySubBlocks = null;

    private final Alignment myPropertyValueAlignment;
    private final Wrap myChildWrap;

    public HJsonBlock(@Nullable HJsonBlock parent,
                     @NotNull ASTNode node,
                     @NotNull HJsonCodeStyleSettings customSettings,
                     @Nullable Alignment alignment,
                     @NotNull Indent indent,
                     @Nullable Wrap wrap,
                     @NotNull SpacingBuilder spacingBuilder) {
        myParent = parent;
        myNode = node;
        myPsiElement = node.getPsi();
        myAlignment = alignment;
        myIndent = indent;
        myWrap = wrap;
        mySpacingBuilder = spacingBuilder;
        myCustomSettings = customSettings;

        if (myPsiElement instanceof HJsonObject) {
            myChildWrap = Wrap.createWrap(myCustomSettings.OBJECT_WRAPPING, true);
        }
        else if (myPsiElement instanceof HJsonArray) {
            myChildWrap = Wrap.createWrap(myCustomSettings.ARRAY_WRAPPING, true);
        }
        else {
            myChildWrap = null;
        }

        myPropertyValueAlignment = myPsiElement instanceof HJsonObject ? Alignment.createAlignment(true) : null;
    }

    @Override
    public ASTNode getNode() {
        return myNode;
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            int propertyAlignment = myCustomSettings.PROPERTY_ALIGNMENT;
            ASTNode[] children = myNode.getChildren(null);
            mySubBlocks = new ArrayList<>(children.length);
            for (ASTNode child: children) {
                if (isWhitespaceOrEmpty(child)) continue;
                mySubBlocks.add(makeSubBlock(child, propertyAlignment));
            }
        }
        return mySubBlocks;
    }

    private Block makeSubBlock(@NotNull ASTNode childNode, int propertyAlignment) {
        Indent indent = Indent.getNoneIndent();
        Alignment alignment = null;
        Wrap wrap = null;

        if (hasElementType(myNode, HJSON_CONTAINERS)) {
            if (hasElementType(childNode, COMMA)) {
                wrap = Wrap.createWrap(WrapType.NONE, true);
            }
            else if (!hasElementType(childNode, JSON_ALL_BRACES)){
                assert myChildWrap != null;
                wrap = myChildWrap;
                indent = Indent.getNormalIndent();
            }
            else if (hasElementType(childNode, JSON_OPEN_BRACES)) {
                if (HJsonPsiUtil.isPropertyValue(myPsiElement) && propertyAlignment == ALIGN_PROPERTY_ON_VALUE) {
                    // WEB-13587 Align compound values on opening brace/bracket, not the whole block
                    assert myParent != null && myParent.myParent != null && myParent.myParent.myPropertyValueAlignment != null;
                    alignment = myParent.myParent.myPropertyValueAlignment;
                }
            }
        }
        // Handle properties alignment
        else if (hasElementType(myNode, MEMBER) ) {
            assert myParent != null && myParent.myPropertyValueAlignment != null;
            if (hasElementType(childNode, COLON) && propertyAlignment == ALIGN_PROPERTY_ON_COLON) {
                alignment = myParent.myPropertyValueAlignment;
            }
            else if (HJsonPsiUtil.isPropertyValue(childNode.getPsi()) && propertyAlignment == ALIGN_PROPERTY_ON_VALUE) {
                if (!hasElementType(childNode, HJSON_CONTAINERS)) {
                    alignment = myParent.myPropertyValueAlignment;
                }
            }
        }
        return new HJsonBlock(this, childNode, myCustomSettings, alignment, indent, wrap, mySpacingBuilder);
    }

    @Nullable
    @Override
    public Wrap getWrap() {
        return myWrap;
    }

    @Nullable
    @Override
    public Indent getIndent() {
        return myIndent;
    }

    @Nullable
    @Override
    public Alignment getAlignment() {
        return myAlignment;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return mySpacingBuilder.getSpacing(this, child1, child2);
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        if (hasElementType(myNode, HJSON_CONTAINERS)) {
            // WEB-13675: For some reason including alignment in child attributes causes
            // indents to consist solely of spaces when both USE_TABS and SMART_TAB
            // options are enabled.
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }
        else if (myNode.getPsi() instanceof PsiFile) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }
        // Will use continuation indent for cases like { "foo"<caret>  }
        return new ChildAttributes(null, null);
    }

    @Override
    public boolean isIncomplete() {
        final ASTNode lastChildNode = myNode.getLastChildNode();
        if (hasElementType(myNode, OBJECT)) {
            return lastChildNode != null && lastChildNode.getElementType() != R_CURLY;
        }
        else if (hasElementType(myNode, ARRAY)) {
            return lastChildNode != null && lastChildNode.getElementType() != R_BRACKET;
        }
        else if (hasElementType(myNode, MEMBER)) {
            return ((HJsonMember)myPsiElement).getValue() == null;
        }
        return false;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    private static boolean isWhitespaceOrEmpty(ASTNode node) {
        return node.getElementType() == TokenType.WHITE_SPACE || node.getTextLength() == 0;
    }
}
