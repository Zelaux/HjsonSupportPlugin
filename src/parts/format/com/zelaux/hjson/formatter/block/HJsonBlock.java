package com.zelaux.hjson.formatter.block;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonObject;
import com.zelaux.hjson.psi.HJsonPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.zelaux.hjson.HJsonElementTypes.*;
import static com.zelaux.hjson.formatter.HJsonCodeStyleSettings.ALIGN_PROPERTY_ON_COLON;
import static com.zelaux.hjson.formatter.HJsonCodeStyleSettings.ALIGN_PROPERTY_ON_VALUE;
import static com.zelaux.hjson.psi.HJsonPsiUtil.hasElementType;
import static com.zelaux.hjson.psi.HJsonTokens.HJSON_CONTAINERS;

public class HJsonBlock implements ASTBlock {
    protected static final TokenSet HJSON_OPEN_BRACES = TokenSet.create(L_BRACKET, L_CURLY);
    protected static final TokenSet HJSON_CLOSE_BRACES = TokenSet.create(R_BRACKET, R_CURLY);
    protected static final TokenSet HJSON_ALL_BRACES = TokenSet.orSet(HJSON_OPEN_BRACES, HJSON_CLOSE_BRACES);

    protected final HJsonBlock myParent;

    protected final ASTNode myNode;
    protected final PsiElement myPsiElement;
    protected final Alignment myAlignment;
    protected final Indent myIndent;
    protected final Wrap myWrap;
    protected final HJsonCodeStyleSettings myCustomSettings;
    protected final SpacingBuilder mySpacingBuilder;
    private final Alignment myPropertyValueAlignment;
    private final Wrap myChildWrap;
    // lazy initialized on first call to #getSubBlocks()
    protected List<Block> mySubBlocks = null;
    private Alignment myArrayChildAlignment;

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
        } else if (myPsiElement instanceof HJsonArray) {
            myChildWrap = Wrap.createWrap(myCustomSettings.ARRAY_WRAPPING, true);
        } else {
            myChildWrap = null;
        }

        myPropertyValueAlignment = myPsiElement instanceof HJsonObject ? Alignment.createAlignment(true) : null;
    }

    private static boolean isWhitespaceOrEmpty(ASTNode node) {
        return node.getElementType() == TokenType.WHITE_SPACE || node.getTextLength() == 0;
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
            for (ASTNode child : children) {
                if (isWhitespaceOrEmpty(child)) continue;
                Block e = makeSubBlock(child, propertyAlignment);
                /*if (e instanceof MultilineStringBlock) {
                    mySubBlocks.addAll(e.getSubBlocks());
                } else*/
                mySubBlocks.add(e);
            }
        }
        return mySubBlocks;
    }

    private Block makeSubBlock(@NotNull ASTNode childNode, int propertyAlignment) {
        Indent indent = Indent.getNoneIndent();
        Alignment alignment = null;//Alignment.createAlignment();
        Wrap wrap = null;

        if (hasElementType(myNode, HJSON_CONTAINERS)) {
            if (hasElementType(childNode, COMMA)) {
                wrap = Wrap.createWrap(WrapType.NONE, true);
            } else if (!hasElementType(childNode, HJSON_ALL_BRACES)) {
                assert myChildWrap != null;
                wrap = myChildWrap;
                indent = Indent.getNormalIndent();
            } else if (hasElementType(childNode, HJSON_OPEN_BRACES)) {
                if (HJsonPsiUtil.isPropertyValue(myPsiElement) && propertyAlignment == ALIGN_PROPERTY_ON_VALUE) {
                    // WEB-13587 Align compound values on opening brace/bracket, not the whole block
                    assert myParent != null && myParent.myParent != null && myParent.myParent.myPropertyValueAlignment != null;
                    alignment = myParent.myParent.myPropertyValueAlignment;
                }else if (myNode.getElementType()==ARRAY){
                    alignment=myArrayChildAlignment();
                }
            }
        }
        // Handle properties alignment
        else if (hasElementType(myNode, MEMBER)) {
            assert myParent != null && myParent.myPropertyValueAlignment != null;
            if (hasElementType(childNode, COLON) && propertyAlignment == ALIGN_PROPERTY_ON_COLON) {
                alignment = myParent.myPropertyValueAlignment;
            } else if (HJsonPsiUtil.isPropertyValue(childNode.getPsi()) && propertyAlignment == ALIGN_PROPERTY_ON_VALUE) {
                if (!hasElementType(childNode, HJSON_CONTAINERS)) {
                    alignment = myParent.myPropertyValueAlignment;
                }
            } else if (hasElementType(childNode, MEMBER_VALUE)) {
                ASTNode node = childNode.getFirstChildNode();
                if (node != null && hasElementType(node, MULTILINE_STRING)) {
//                    indent = Indent.getContinuationIndent();
//                    alignment=Alignment.createChildAlignment(myAlignment);
//                    return new MultilineStringBlock(this, node, myCustomSettings, alignment, indent, wrap, mySpacingBuilder);
                }
            }
        }
        if (hasElementType(childNode, MULTILINE_STRING)) {
//            wrap=Wrap.createWrap(WrapType.ALWAYS,true);
//            if (!hasElementType(myNode, ARRAY))
                return new MultilineStringBlock(this, childNode, myCustomSettings, myParent.myPropertyValueAlignment, Indent.getNoneIndent(), wrap, mySpacingBuilder);
        }
        //TODO add HJsonBlock for lines in multilineString
        return new HJsonBlock(this, childNode, myCustomSettings, alignment, indent, wrap, mySpacingBuilder);
    }

    private Alignment myArrayChildAlignment() {
        if(myArrayChildAlignment==null)return myArrayChildAlignment=Alignment.createAlignment();
        return myArrayChildAlignment;
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
        } else if (myNode.getPsi() instanceof PsiFile) {
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
        } else if (hasElementType(myNode, ARRAY)) {
            return lastChildNode != null && lastChildNode.getElementType() != R_BRACKET;
        } else if (hasElementType(myNode, MEMBER)) {
            return ((HJsonMember) myPsiElement).getValue() == null;
        }
        return false;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
