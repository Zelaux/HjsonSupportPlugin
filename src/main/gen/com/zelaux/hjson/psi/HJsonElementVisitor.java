// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;

public class HJsonElementVisitor extends PsiElementVisitor {

  public void visitArray(@NotNull HJsonArray o) {
    visitValue(o);
    // visitContainer(o);
    // visitValue(o);
  }

  public void visitBooleanLiteral(@NotNull HJsonBooleanLiteral o) {
    visitLiteral(o);
  }

  public void visitComment(@NotNull HJsonComment o) {
    visitElement(o);
  }

  public void visitContainer(@NotNull HJsonContainer o) {
    visitElement(o);
  }

  public void visitJsonString(@NotNull HJsonJsonString o) {
    visitStringLiteral(o);
  }

  public void visitLiteral(@NotNull HJsonLiteral o) {
    visitValue(o);
  }

  public void visitMember(@NotNull HJsonMember o) {
    visitElement(o);
    // visitElement(o);
    // visitPsiNamedElement(o);
  }

  public void visitMemberName(@NotNull HJsonMemberName o) {
    visitElement(o);
  }

  public void visitMemberValue(@NotNull HJsonMemberValue o) {
    visitElement(o);
  }

  public void visitMultilineString(@NotNull HJsonMultilineString o) {
    visitStringLiteral(o);
  }

  public void visitNullLiteral(@NotNull HJsonNullLiteral o) {
    visitLiteral(o);
  }

  public void visitNumberLiteral(@NotNull HJsonNumberLiteral o) {
    visitLiteral(o);
  }

  public void visitObject(@NotNull HJsonObject o) {
    visitElement(o);
    // visitContainer(o);
    // visitValue(o);
  }

  public void visitObjectFull(@NotNull HJsonObjectFull o) {
    visitValue(o);
    // visitObject(o);
  }

  public void visitQuoteLessString(@NotNull HJsonQuoteLessString o) {
    visitStringLiteral(o);
  }

  public void visitStringLiteral(@NotNull HJsonStringLiteral o) {
    visitLiteral(o);
  }

  public void visitValue(@NotNull HJsonValue o) {
    visitElement(o);
  }

  public void visitElement(@NotNull HJsonElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
