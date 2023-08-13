package com.zelaux.hjson;

import com.intellij.icons.AllIcons;
import com.intellij.json.JsonBundle;
import com.intellij.json.JsonFileType;
import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class HJsonFileType extends LanguageFileType {
    public static final HJsonFileType INSTANCE = new HJsonFileType();
    public static final String DEFAULT_EXTENSION = "hjson";

    protected HJsonFileType(Language language) {
        super(language);
    }

    protected HJsonFileType(Language language, boolean secondary) {
        super(language, secondary);
    }

    protected HJsonFileType() {
        super(HJsonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "HJSON";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "HJSON";
//        return JsonBundle.message("filetype.hjson.description");
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return HJsonIcons.FileType;
    }
}
