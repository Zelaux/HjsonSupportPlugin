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

public class HJsonQuotelessStringImpl extends HJsonStringLiteralImpl implements HJsonQuotelessString {

  public HJsonQuotelessStringImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitQuotelessString(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getQuotelessStringToken() {
    return findNotNullChildByType(QUOTELESS_STRING_TOKEN);
  }

}
