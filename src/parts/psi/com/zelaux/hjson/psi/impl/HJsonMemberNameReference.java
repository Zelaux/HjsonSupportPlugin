package com.zelaux.hjson.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonMemberName;
import com.zelaux.hjson.psi.HJsonValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonMemberNameReference  implements PsiReference {
    private final HJsonMember myMember;

    public HJsonMemberNameReference(@NotNull HJsonMember member) {
        myMember = member;
    }

    @NotNull
    @Override
    public PsiElement getElement() {
        return myMember;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        final @NotNull HJsonMemberName nameElement = myMember.getMemberName();
        // Either value of string with quotes stripped or element's text as is
        return ElementManipulators.getValueTextRange(nameElement.getStringLiteral());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return myMember;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return myMember.getName();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myMember.setName(newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (!(element instanceof HJsonMember)) {
            return false;
        }
        // May reference to the property with the same name for compatibility with JavaScript JSON support
        final HJsonMember otherProperty = (HJsonMember)element;
        final PsiElement selfResolve = resolve();
        return otherProperty.getName().equals(getCanonicalText()) && selfResolve != otherProperty;
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
