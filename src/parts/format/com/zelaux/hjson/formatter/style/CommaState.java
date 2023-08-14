package com.zelaux.hjson.formatter.style;

import com.zelaux.hjson.HJsonBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public enum CommaState implements SettingStyleOption {
    KEEP(0, "formatter.comma.state.keep"),
    REMOVE(1, "formatter.comma.state.remove"),
    ADD(2, "formatter.comma.state.add");
    public static SettingStyleOption.Info info = SettingStyleOption.infoOf(values());
    public static CommaState[] all=values();
    @PropertyKey(resourceBundle = HJsonBundle.BUNDLE)
    private final String myKey;
public final int id;

    CommaState(int id, @NotNull @PropertyKey(resourceBundle = HJsonBundle.BUNDLE) String key) {
        myKey = key;
        this.id = id;
    }

    @NotNull
    public String getDescription() {
        return HJsonBundle.message(myKey);
    }

    public int getId() {
        return id;
    }
}
