package com.zelaux.hjson.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveVisitor;
import com.zelaux.hjson.psi.HJsonElementVisitor;
import org.jetbrains.annotations.NotNull;

public class HJsonRecursiveElementVisitor extends HJsonElementVisitor implements PsiRecursiveVisitor {
    @Override
    public void visitElement(@NotNull PsiElement o) {
        o.acceptChildren(this);
    }
}
