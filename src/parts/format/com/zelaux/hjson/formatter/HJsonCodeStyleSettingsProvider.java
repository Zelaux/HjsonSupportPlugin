package com.zelaux.hjson.formatter;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.HJsonLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonCodeStyleSettingsProvider  extends CodeStyleSettingsProvider {
    @Override
    public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings,
                                                             @NotNull CodeStyleSettings originalSettings) {
        return new CodeStyleAbstractConfigurable(settings, originalSettings, HJsonBundle.message("settings.display.name.hjson")) {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                final Language language = HJsonLanguage.INSTANCE;
                final CodeStyleSettings currentSettings = getCurrentSettings();
                return new TabbedLanguageCodeStylePanel(language, currentSettings, settings) {
                    @Override
                    protected void initTabs(CodeStyleSettings settings) {
                        addIndentOptionsTab(settings);
                        addSpacesTab(settings);
                        addBlankLinesTab(settings);
                        addWrappingAndBracesTab(settings);
                    }
                };
            }

            @Override
            public @NotNull String getHelpTopic() {
                return "reference.settingsdialog.codestyle.hjson";
            }
        };
    }

    @Nullable
    @Override
    public String getConfigurableDisplayName() {
        return HJsonLanguage.INSTANCE.getDisplayName();
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new HJsonCodeStyleSettings(settings);
    }

    @Override
    public @Nullable Language getLanguage() {
        return HJsonLanguage.INSTANCE;
    }
}
