package com.zelaux.hjson;

import com.intellij.json.JsonLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class HJsonTokenType  extends IElementType {
  public HJsonTokenType(@NotNull @NonNls String debugName) {
        super(debugName, HJsonLanguage.INSTANCE);
    }
}
