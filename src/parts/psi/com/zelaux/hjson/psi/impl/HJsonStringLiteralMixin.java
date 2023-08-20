package com.zelaux.hjson.psi.impl;

import com.intellij.json.psi.impl.JSStringLiteralEscaper;
import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper;
import com.zelaux.hjson.psi.HJsonMultilineString;
import com.zelaux.hjson.psi.impl.tree.injected.MultilineStringLiteralEscaper;
import org.jetbrains.annotations.NotNull;

public class HJsonStringLiteralMixin extends HJsonLiteralImpl implements PsiLanguageInjectionHost {
    protected HJsonStringLiteralMixin(ASTNode node) {
        super(node);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        ASTNode valueNode = getNode().getFirstChildNode();
        assert valueNode instanceof LeafElement;
        ((LeafElement)valueNode).replaceWithText(text);
        return this;
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        if(this instanceof HJsonMultilineString){
            //noinspection rawtypes,unchecked
            return new MultilineStringLiteralEscaper((HJsonMultilineString) this);
        }
        return new StringLiteralEscaper<>(this);
    }

    @Override
    public void subtreeChanged() {
        putUserData(HJsonPsiImplUtils.STRING_FRAGMENTS, null);
    }
}

