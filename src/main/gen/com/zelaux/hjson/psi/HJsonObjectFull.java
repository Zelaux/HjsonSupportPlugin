// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface HJsonObjectFull extends HJsonValue, HJsonObject {

  @NotNull
  List<HJsonMember> getMemberList();

  @Nullable HJsonMember findMember(@NotNull String name);

  @Nullable ItemPresentation getPresentation();

}
