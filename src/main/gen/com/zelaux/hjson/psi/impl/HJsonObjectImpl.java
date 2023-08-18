// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.zelaux.hjson.HJsonElementTypes.*;
import com.zelaux.hjson.psi.*;
import com.intellij.navigation.ItemPresentation;

public class HJsonObjectImpl extends HJsonObjectMixin implements HJsonObject {

  public HJsonObjectImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitObject(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<HJsonMember> getMemberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HJsonMember.class);
  }

  @Override
  public @Nullable ItemPresentation getPresentation() {
    return HJsonPsiImplUtils.getPresentation(this);
  }

}
