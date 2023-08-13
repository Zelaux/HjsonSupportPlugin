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

public class HJsonContainerImpl extends ASTWrapperPsiElement implements HJsonContainer {

  public HJsonContainerImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HJsonElementVisitor visitor) {
    visitor.visitContainer(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HJsonElementVisitor) accept((HJsonElementVisitor)visitor);
    else super.accept(visitor);
  }

}
