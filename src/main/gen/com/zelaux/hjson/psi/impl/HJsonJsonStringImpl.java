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

public class HJsonJsonStringImpl extends HJsonStringLiteralImpl implements HJsonJsonString {

  public HJsonJsonStringImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitJsonString(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getDoubleQuotedStringToken() {
    return findChildByType(DOUBLE_QUOTED_STRING_TOKEN);
  }

  @Override
  @Nullable
  public PsiElement getSingleQuotedStringToken() {
    return findChildByType(SINGLE_QUOTED_STRING_TOKEN);
  }

}
