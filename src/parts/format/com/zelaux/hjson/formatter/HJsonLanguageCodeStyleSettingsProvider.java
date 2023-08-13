package com.zelaux.hjson.formatter;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.application.options.codeStyle.properties.CodeStyleFieldAccessor;
import com.intellij.application.options.codeStyle.properties.MagicIntegerConstAccessor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.HJsonLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;

import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizableOptions.getInstance;

public class HJsonLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    private static class Holder {
        private static final String[] ALIGN_OPTIONS = Arrays.stream(HJsonCodeStyleSettings.PropertyAlignment.values())
                .map(alignment -> alignment.getDescription())
                .toArray(value -> new String[value]);

        private static final int[] ALIGN_VALUES =
                ArrayUtil.toIntArray(
                        ContainerUtil.map(HJsonCodeStyleSettings.PropertyAlignment.values(), alignment -> alignment.getId()));

        private static final String SAMPLE = "{\n" +
                                             "    \"json literals are\": {\n" +
                                             "        \"strings\": [\"foo\", \"bar\", \"\\u0062\\u0061\\u0072\"],\n" +
                                             "        strings: [\"foo\", \"bar\", \"\\u0062\\u0061\\u0072\"],\n" +
                                             "        \"numbers\": [42, 6.62606975e-34],\n" +
                                             "        \"boolean values\": [true, false,],\n" +
                                             "        \"objects\": {\"null\": null,\"another\": null,}\n" +
                                             "    }\n" +
                                             "}";
    }
    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_WITHIN_BRACKETS",
                    "SPACE_WITHIN_BRACES",
                    "SPACE_AFTER_COMMA",
                    "SPACE_BEFORE_COMMA");
            consumer.renameStandardOption("SPACE_WITHIN_BRACES", HJsonBundle.message("formatter.space_within_braces.label"));
            consumer.showCustomOption(HJsonCodeStyleSettings.class, "SPACE_BEFORE_COLON", HJsonBundle.message("formatter.space_before_colon.label"), getInstance().SPACES_OTHER);
            consumer.showCustomOption(HJsonCodeStyleSettings.class, "SPACE_AFTER_COLON", HJsonBundle.message("formatter.space_after_colon.label"), getInstance().SPACES_OTHER);
        }
        else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
        }
        else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions("RIGHT_MARGIN",
                    "WRAP_ON_TYPING",
                    "KEEP_LINE_BREAKS",
                    "WRAP_LONG_LINES");

            consumer.showCustomOption(HJsonCodeStyleSettings.class,
                    "KEEP_TRAILING_COMMA",
                    HJsonBundle.message("formatter.trailing_comma.label"),
                    getInstance().WRAPPING_KEEP);

            consumer.showCustomOption(HJsonCodeStyleSettings.class,
                    "ARRAY_WRAPPING",
                    HJsonBundle.message("formatter.wrapping_arrays.label"),
                    null,
                    getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES);

            consumer.showCustomOption(HJsonCodeStyleSettings.class,
                    "OBJECT_WRAPPING",
                    HJsonBundle.message("formatter.objects.label"),
                    null,
                    getInstance().WRAP_OPTIONS,
                    CodeStyleSettingsCustomizable.WRAP_VALUES);

            consumer.showCustomOption(HJsonCodeStyleSettings.class,
                    "PROPERTY_ALIGNMENT",
                    HJsonBundle.message("formatter.align.properties.caption"),
                    HJsonBundle.message("formatter.objects.label"),
                    HJsonLanguageCodeStyleSettingsProvider.Holder.ALIGN_OPTIONS,
                    HJsonLanguageCodeStyleSettingsProvider.Holder.ALIGN_VALUES);

        }
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return HJsonLanguage.INSTANCE;
    }

    @Nullable
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return HJsonLanguageCodeStyleSettingsProvider.Holder.SAMPLE;
    }

    @Override
    protected void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
                                     @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.INDENT_SIZE = 2;
        // strip all blank lines by default
        commonSettings.KEEP_BLANK_LINES_IN_CODE = 0;
    }

    @Override
    public @Nullable CodeStyleFieldAccessor getAccessor(@NotNull Object codeStyleObject, @NotNull Field field) {
        if (codeStyleObject instanceof HJsonCodeStyleSettings && field.getName().equals("PROPERTY_ALIGNMENT")) {
            return new MagicIntegerConstAccessor(
                    codeStyleObject, field,
                    new int[] {
                            HJsonCodeStyleSettings.PropertyAlignment.DO_NOT_ALIGN.getId(),
                            HJsonCodeStyleSettings.PropertyAlignment.ALIGN_ON_VALUE.getId(),
                            HJsonCodeStyleSettings.PropertyAlignment.ALIGN_ON_COLON.getId()
                    },
                    new String[] {
                            "do_not_align",
                            "align_on_value",
                            "align_on_colon"
                    }
            );
        }
        return null;
    }
}
