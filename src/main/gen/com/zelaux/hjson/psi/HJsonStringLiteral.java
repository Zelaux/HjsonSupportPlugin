// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;

public interface HJsonStringLiteral extends HJsonLiteral {

  @NotNull List<Pair<TextRange, String>> getTextFragments();

  @NotNull String getValue();

  //WARNING: isPropertyName(...) is skipped
  //matching isPropertyName(HJsonStringLiteral, ...)
  //methods are not found in HJsonPsiImplUtils

  //WARNING: SINGLE_QUOTED_STRING(...) is skipped
  //matching SINGLE_QUOTED_STRING(HJsonStringLiteral, ...)
  //methods are not found in HJsonPsiImplUtils

  //WARNING: DOUBLE_QUOTED_STRING(...) is skipped
  //matching DOUBLE_QUOTED_STRING(HJsonStringLiteral, ...)
  //methods are not found in HJsonPsiImplUtils

  //WARNING: QUOTE_LESS_STRING(...) is skipped
  //matching QUOTE_LESS_STRING(HJsonStringLiteral, ...)
  //methods are not found in HJsonPsiImplUtils

}
