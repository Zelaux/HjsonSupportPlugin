// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.navigation.ItemPresentation;

public interface HJsonMember extends HJsonElement, PsiNamedElement {

  @Nullable
  HJsonMemberValue getMemberValue();

  @NotNull
  PsiElement getMemberName();

  @NotNull String getName();

  @Nullable HJsonValue getValue();

  //WARNING: value(...) is skipped
  //matching value(HJsonMember, ...)
  //methods are not found in HJsonPsiImplUtils

  @Nullable ItemPresentation getPresentation();

}
