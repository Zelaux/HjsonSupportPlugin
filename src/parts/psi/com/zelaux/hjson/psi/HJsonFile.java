package com.zelaux.hjson.psi;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonElement;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonValue;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface HJsonFile  extends HJsonElement, PsiFile {
    /**
     * Returns {@link JsonArray} or {@link JsonObject} value according to JSON standard.
     *
     * @return top-level JSON element if any or {@code null} otherwise
     */
    @Nullable
    HJsonValue getTopLevelValue();

    @NotNull
    List<HJsonValue> getAllTopLevelValues();
}
