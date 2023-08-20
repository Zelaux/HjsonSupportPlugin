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

public class HJsonMultilineStringImpl extends HJsonStringLiteralImpl implements HJsonMultilineString {

  public HJsonMultilineStringImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitMultilineString(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getMultilineStringToken() {
    return findNotNullChildByType(MULTILINE_STRING_TOKEN);
  }

  @Override
  public String[] getLines() {
    return HJsonPsiImplUtils.getLines(this);
  }

  @Override
  public int getIndent() {
    return HJsonPsiImplUtils.getIndent(this);
  }

}
