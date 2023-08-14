package com.zelaux.hjson.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.json.psi.JsonElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class HJsonElementImpl  extends ASTWrapperPsiElement {

    public HJsonElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        final String className = getClass().getSimpleName();
        return StringUtil.trimEnd(className, "Impl");
    }
}