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

public class HJsonArrayImpl extends HJsonValueImpl implements HJsonArray {

  public HJsonArrayImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitArray(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<HJsonValue> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, HJsonValue.class);
  }

  @Override
  public @Nullable ItemPresentation getPresentation() {
    return HJsonPsiImplUtils.getPresentation(this);
  }

}
