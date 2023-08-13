package com.zelaux.hjson;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;

public class HJsonLanguage extends Language {
    public static final HJsonLanguage INSTANCE = new HJsonLanguage();

    protected HJsonLanguage(String ID, String... mimeTypes) {
        super(INSTANCE, ID, mimeTypes);
    }

    private HJsonLanguage() {
        super("HJSON", "application/hjson", "application/vnd.api+hjson", "application/hal+hjson", "application/ld+hjson");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    public boolean hasPermissiveStrings() { return false; }
}
