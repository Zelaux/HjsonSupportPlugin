package com.zelaux.hjson.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.zelaux.hjson.psi.HJsonElementGenerator;
import com.zelaux.hjson.psi.HJsonMember;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

 abstract class HJsonMemberMixin  extends HJsonElementImpl implements HJsonMember {
     HJsonMemberMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        final HJsonElementGenerator generator = new HJsonElementGenerator(getProject());
        // Strip only both quotes in case user wants some exotic name like key'
        getMemberName().getNameElement().replace(generator.createStringLiteral(StringUtil.unquoteString(name)));
        return this;
    }

    @Override
    public PsiReference getReference() {
        return new HJsonMemberNameReference(this);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        final PsiReference[] fromProviders = ReferenceProvidersRegistry.getReferencesFromProviders(this);
        return ArrayUtil.prepend(new HJsonMemberNameReference(this), fromProviders);
    }
}
