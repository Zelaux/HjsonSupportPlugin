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

public class HJsonCommentImpl extends ASTWrapperPsiElement implements HJsonComment {

  public HJsonCommentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitComment(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getBlockComment() {
    return findChildByType(BLOCK_COMMENT);
  }

  @Override
  @Nullable
  public PsiElement getLineComment() {
    return findChildByType(LINE_COMMENT);
  }

}
