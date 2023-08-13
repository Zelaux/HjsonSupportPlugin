package com.zelaux.hjson.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonValue;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.zelaux.hjson.psi.HJsonFile;
import com.zelaux.hjson.psi.HJsonValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HJsonFileImpl  extends PsiFileBase implements HJsonFile {

    public HJsonFileImpl(FileViewProvider fileViewProvider, Language language) {
        super(fileViewProvider, language);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return getViewProvider().getFileType();
    }

    @Nullable
    @Override
    public HJsonValue getTopLevelValue() {
        return PsiTreeUtil.getChildOfType(this, HJsonValue.class);
    }

    @NotNull
    @Override
    public List<HJsonValue> getAllTopLevelValues() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, HJsonValue.class);
    }

    @Override
    public String toString() {
        return "HJsonFile: " + getName();
    }
}