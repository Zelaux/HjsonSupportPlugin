package com.zelaux.hjson.psi.impl;

import com.intellij.json.psi.impl.JSStringLiteralEscaper;
import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.LeafElement;
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
        return new JSStringLiteralEscaper<PsiLanguageInjectionHost>(this) {
            @Override
            protected boolean isRegExpLiteral() {
                return false;
            }
        };
    }

    @Override
    public void subtreeChanged() {
        putUserData(HJsonPsiImplUtils.STRING_FRAGMENTS, null);
    }
}

