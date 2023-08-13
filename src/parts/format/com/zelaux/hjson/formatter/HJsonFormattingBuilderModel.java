package com.zelaux.hjson.formatter;

import com.intellij.formatting.*;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.zelaux.hjson.HJsonLanguage;
import org.jetbrains.annotations.NotNull;

import static com.zelaux.hjson.HJsonElementTypes.*;


public class HJsonFormattingBuilderModel implements FormattingModelBuilder {

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        HJsonCodeStyleSettings customSettings = settings.getCustomSettings(HJsonCodeStyleSettings.class);
        SpacingBuilder spacingBuilder = createSpacingBuilder(settings);
        final HJsonBlock block =
                new HJsonBlock(null, formattingContext.getNode(), customSettings, null, Indent.getSmartIndent(Indent.Type.CONTINUATION), null,
                        spacingBuilder);
        return FormattingModelProvider.createFormattingModelForPsiFile(formattingContext.getContainingFile(), block, settings);
    }

    @NotNull
    static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
        final HJsonCodeStyleSettings jsonSettings = settings.getCustomSettings(HJsonCodeStyleSettings.class);
        final CommonCodeStyleSettings commonSettings = settings.getCommonSettings(HJsonLanguage.INSTANCE);

        final int spacesBeforeComma = commonSettings.SPACE_BEFORE_COMMA ? 1 : 0;
        final int spacesBeforeColon = jsonSettings.SPACE_BEFORE_COLON ? 1 : 0;
        final int spacesAfterColon = jsonSettings.SPACE_AFTER_COLON ? 1 : 0;

        return new SpacingBuilder(settings, HJsonLanguage.INSTANCE)
                .before(COLON).spacing(spacesBeforeColon, spacesBeforeColon, 0, false, 0)
                .after(COLON).spacing(spacesAfterColon, spacesAfterColon, 0, false, 0)
                .withinPair(L_BRACKET, R_BRACKET).spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, true)
                .withinPair(L_CURLY, R_CURLY).spaceIf(commonSettings.SPACE_WITHIN_BRACES, true)
                .before(COMMA).spacing(spacesBeforeComma, spacesBeforeComma, 0, false, 0)
                .after(COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA);
    }
}
