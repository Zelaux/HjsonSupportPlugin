// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HJsonMultilineString extends HJsonStringLiteral {

  @NotNull
  PsiElement getMultilineStringToken();

  String[] getLines();

  int getIndent();

}
