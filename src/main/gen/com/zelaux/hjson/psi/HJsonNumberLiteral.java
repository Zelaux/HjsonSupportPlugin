// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HJsonNumberLiteral extends HJsonLiteral {

  @NotNull
  PsiElement getNumberToken();

  //WARNING: NUMBER(...) is skipped
  //matching NUMBER(HJsonNumberLiteral, ...)
  //methods are not found in HJsonPsiImplUtils

  double getValue();

}
