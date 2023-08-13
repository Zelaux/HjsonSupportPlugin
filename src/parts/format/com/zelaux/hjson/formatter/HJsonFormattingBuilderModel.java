package com.zelaux.hjson.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.util.PsiTreeUtil;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.HJsonFile;
import com.zelaux.hjson.psi.HJsonObject;
import com.zelaux.hjson.psi.HJsonObjectFull;
import org.jetbrains.annotations.NotNull;

import static com.zelaux.hjson.HJsonElementTypes.*;


public class HJsonFormattingBuilderModel implements FormattingModelBuilder {

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

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();
        HJsonCodeStyleSettings customSettings = settings.getCustomSettings(HJsonCodeStyleSettings.class);
        SpacingBuilder spacingBuilder = createSpacingBuilder(settings);
        Indent indent = Indent.getSmartIndent(Indent.Type.CONTINUATION);
        ASTNode node = formattingContext.getNode();
        PsiElement psi = node.getPsi();
       /* if (psi instanceof HJsonFile) {
            HJsonObjectFull child = PsiTreeUtil.getChildOfType(psi, HJsonObjectFull.class);
            if (child == null) {
//                node=PsiTreeUtil.getChildOfType(psi, HJsonObject.class).getNode();
//                indent = Indent.getContinuationWithoutFirstIndent();
            }
        }*/
        final HJsonBlock block =
                new HJsonBlock(null, node, customSettings, Alignment.createAlignment(), indent, null,
                        spacingBuilder);
        return FormattingModelProvider.createFormattingModelForPsiFile(formattingContext.getContainingFile(), block, settings);
    }
}
