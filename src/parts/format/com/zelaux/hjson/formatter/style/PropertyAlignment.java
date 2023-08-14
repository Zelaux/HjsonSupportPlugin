package com.zelaux.hjson.formatter.style;

import com.zelaux.hjson.HJsonBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public enum PropertyAlignment implements SettingStyleOption {
    DO_NOT_ALIGN(0, "formatter.align.properties.none"),
    ALIGN_ON_VALUE(1, "formatter.align.properties.on.value"),
    ALIGN_ON_COLON(2, "formatter.align.properties.on.colon");
    public static Info info = SettingStyleOption.infoOf(values());
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
