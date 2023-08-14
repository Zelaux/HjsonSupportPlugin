package com.zelaux.hjson.formatter.style;

import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.Arrays;

public interface SettingStyleOption {

    static String[] descriptionsOf(SettingStyleOption... options) {
        return Arrays.stream(options)
                .map(SettingStyleOption::getDescription)
                .toArray(String[]::new);
    }

    static int[] idsOf(SettingStyleOption... options) {
        return ArrayUtil.toIntArray(
                ContainerUtil.map(options, SettingStyleOption::getId));
    }

    static Info infoOf(SettingStyleOption... options) {
        return new Info(options);
    }

    String getDescription();

    int getId();

     class Info {
        public final int[] ids;
        public final String[] descriptions;

        private Info(SettingStyleOption... descriptions) {
            this.ids = idsOf(descriptions);
            this.descriptions = descriptionsOf(descriptions);
        }
    }
}
