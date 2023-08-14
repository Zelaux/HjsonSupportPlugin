// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.zelaux.hjson.HJsonElementTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.zelaux.hjson.psi.*;

public class HJsonMemberNameImpl extends ASTWrapperPsiElement implements HJsonMemberName {

  public HJsonMemberNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitMemberName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public HJsonStringLiteral getStringLiteral() {
    return findNotNullChildByClass(HJsonStringLiteral.class);
  }

  @Override
  public @NotNull String getName() {
    return HJsonPsiImplUtils.getName(this);
  }

}
