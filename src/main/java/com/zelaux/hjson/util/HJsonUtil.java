package com.zelaux.hjson.util;

import com.intellij.ide.scratch.RootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtils;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HJsonUtil { private HJsonUtil() {
    // empty
}

    /**
     * Clone of C# "as" operator.
     * Checks if expression has correct type and casts it if it has. Returns null otherwise.
     * It saves coder from "instanceof / cast" chains.
     *
     * Copied from PyCharm's {@code PyUtil}.
     *
     * @param expression expression to check
     * @param cls        class to cast
     * @param <T>        class to cast
     * @return expression casted to appropriate type (if could be casted). Null otherwise.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T as(@Nullable final Object expression, @NotNull final Class<T> cls) {
        if (expression == null) {
            return null;
        }
        if (cls.isAssignableFrom(expression.getClass())) {
            return (T)expression;
        }
        return null;
    }


    public static boolean isArrayElement(@NotNull PsiElement element) {
        return element instanceof HJsonValue && element.getParent() instanceof HJsonArray;
    }

    public static int getArrayIndexOfItem(@NotNull PsiElement e) {
        PsiElement parent = e.getParent();
        if (!(parent instanceof HJsonArray)) return -1;
        List<HJsonValue> elements = ((HJsonArray)parent).getValueList();
        for (int i = 0; i < elements.size(); i++) {
            if (e == elements.get(i)) {
                return i;
            }
        }
        return -1;
    }

    @Contract("null -> null")
    public static @Nullable HJsonObject getTopLevelObject(@Nullable HJsonFile jsonFile) {
        return jsonFile != null ? ObjectUtils.tryCast(jsonFile.getTopLevelValue(), HJsonObject.class) : null;
    }

    public static boolean isJsonFile(@NotNull VirtualFile file, @Nullable Project project) {
        FileType type = file.getFileType();
        if (type instanceof LanguageFileType && ((LanguageFileType)type).getLanguage() instanceof HJsonLanguage) return true;
        if (project == null || !ScratchUtil.isScratch(file)) return false;
        RootType rootType = ScratchFileService.findRootType(file);
        return rootType != null && rootType.substituteLanguage(project, file) instanceof HJsonLanguage;
    }
}
