package com.zelaux.hjson.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.formatter.style.PropertyAlignment;
import org.intellij.lang.annotations.MagicConstant;

public class HJsonCodeStyleSettings extends CustomCodeStyleSettings {

    public static final int DO_NOT_ALIGN_PROPERTY = PropertyAlignment.DO_NOT_ALIGN.getId();
    public static final int ALIGN_PROPERTY_ON_VALUE = PropertyAlignment.ALIGN_ON_VALUE.getId();
    public static final int ALIGN_PROPERTY_ON_COLON = PropertyAlignment.ALIGN_ON_COLON.getId();

    public boolean SPACE_AFTER_COLON = true;
    public boolean SPACE_BEFORE_COLON = false;
    /**
     * Contains value of {@link PropertyAlignment#getId()}
     *
     * @see #DO_NOT_ALIGN_PROPERTY
     * @see #ALIGN_PROPERTY_ON_VALUE
     * @see #ALIGN_PROPERTY_ON_COLON
     */
    public int PROPERTY_ALIGNMENT = PropertyAlignment.DO_NOT_ALIGN.getId();
    @MagicConstant(flags = {
            CommonCodeStyleSettings.DO_NOT_WRAP,
            CommonCodeStyleSettings.WRAP_ALWAYS,
            CommonCodeStyleSettings.WRAP_AS_NEEDED,
            CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM
    })
    @CommonCodeStyleSettings.WrapConstant
    public int OBJECT_WRAPPING = CommonCodeStyleSettings.WRAP_ALWAYS;
    // This was default policy for array elements wrapping in JavaScript's JSON.
    // CHOP_DOWN_IF_LONG seems more appropriate however for short arrays.
    @MagicConstant(flags = {
            CommonCodeStyleSettings.DO_NOT_WRAP,
            CommonCodeStyleSettings.WRAP_ALWAYS,
            CommonCodeStyleSettings.WRAP_AS_NEEDED,
            CommonCodeStyleSettings.WRAP_ON_EVERY_ITEM
    })
    @CommonCodeStyleSettings.WrapConstant
    public int ARRAY_WRAPPING = CommonCodeStyleSettings.WRAP_ALWAYS;
    private int COMMAS = CommaState.KEEP.getId();
    private int TRAILING_COMMA = CommaState.KEEP.id;

    public HJsonCodeStyleSettings(CodeStyleSettings container) {
        super(HJsonLanguage.INSTANCE.getID(), container);
    }

    // TODO: check whether it's possible to migrate CustomCodeStyleSettings to newer com.intellij.util.xmlb.XmlSerializer

    public CommaState commas() {
        return CommaState.all[COMMAS];
    }

    public HJsonCodeStyleSettings commas(CommaState value) {
        this.COMMAS = value.id;
        return this;
    }

    public CommaState trailingComma() {
        return CommaState.all[TRAILING_COMMA];
    }

    public HJsonCodeStyleSettings trailingComma(CommaState trailingComma) {
        this.TRAILING_COMMA = trailingComma.id;
        return this;
    }
}
