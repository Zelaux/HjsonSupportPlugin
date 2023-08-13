// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.navigation.ItemPresentation;

public interface HJsonMember extends HJsonElement, PsiNamedElement {

  @NotNull
  HJsonMemberName getMemberName();

  @NotNull String getName();

  @Nullable HJsonValue getValue();

  @Nullable ItemPresentation getPresentation();

}
