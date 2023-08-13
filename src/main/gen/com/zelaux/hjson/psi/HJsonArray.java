// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface HJsonArray extends HJsonValue {

  @NotNull
  List<HJsonSeparator> getSeparatorList();

  @NotNull
  List<HJsonValue> getValueList();

}
