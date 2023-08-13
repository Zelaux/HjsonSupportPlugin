package com.zelaux.hjson.formatter;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.HJsonLanguage;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class HJsonCodeStyleSettings extends CustomCodeStyleSettings {

    public static final int DO_NOT_ALIGN_PROPERTY = HJsonCodeStyleSettings.PropertyAlignment.DO_NOT_ALIGN.getId();
    public static final int ALIGN_PROPERTY_ON_VALUE = HJsonCodeStyleSettings.PropertyAlignment.ALIGN_ON_VALUE.getId();
    public static final int ALIGN_PROPERTY_ON_COLON = HJsonCodeStyleSettings.PropertyAlignment.ALIGN_ON_COLON.getId();

    public boolean SPACE_AFTER_COLON = true;
    public boolean SPACE_BEFORE_COLON = false;
    public boolean KEEP_TRAILING_COMMA = false;

    // TODO: check whether it's possible to migrate CustomCodeStyleSettings to newer com.intellij.util.xmlb.XmlSerializer
    /**
     * Contains value of {@link HJsonCodeStyleSettings.PropertyAlignment#getId()}
     *
     * @see #DO_NOT_ALIGN_PROPERTY
     * @see #ALIGN_PROPERTY_ON_VALUE
     * @see #ALIGN_PROPERTY_ON_COLON
     */
    public int PROPERTY_ALIGNMENT = HJsonCodeStyleSettings.PropertyAlignment.DO_NOT_ALIGN.getId();

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

    public HJsonCodeStyleSettings(CodeStyleSettings container) {
        super(HJsonLanguage.INSTANCE.getID(), container);
    }

    public enum PropertyAlignment {
        DO_NOT_ALIGN(0, "formatter.align.properties.none"),
        ALIGN_ON_VALUE(1, "formatter.align.properties.on.value"),
        ALIGN_ON_COLON(2, "formatter.align.properties.on.colon");
        @PropertyKey(resourceBundle = HJsonBundle.BUNDLE)
        private final String myKey;
        private final int myId;

        PropertyAlignment(int id, @NotNull @PropertyKey(resourceBundle = HJsonBundle.BUNDLE) String key) {
            myKey = key;
            myId = id;
        }

        @NotNull
        public String getDescription() {
            return HJsonBundle.message(myKey);
        }

        public int getId() {
            return myId;
        }
    }
}
