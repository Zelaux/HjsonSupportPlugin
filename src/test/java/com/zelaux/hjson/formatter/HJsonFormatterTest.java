package com.zelaux.hjson.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.formatter.style.CommaState;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

@SuppressWarnings("JUnitMixedFramework")
public class HJsonFormatterTest extends LightJavaCodeInsightFixtureTestCase {
    /**
     * @return path to test data file directory relative to working directory in the run configuration for this test.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/insight/formatter";
    }

    @Test
    public void testFormatter() {
        myFixture.configureByFile("0-data.hjson");
        CommonCodeStyleSettings languageSettings = CodeStyle.getLanguageSettings(myFixture.getFile());
        languageSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
        CodeStyle.getLanguageSettings(myFixture.getFile()).KEEP_BLANK_LINES_IN_CODE = 2;
        HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(myFixture.getFile(), HJsonCodeStyleSettings.class);
        settings
                .trailingComma(CommaState.KEEP)
                .commas(CommaState.KEEP)
        ;
        WriteCommandAction.writeCommandAction(getProject()).run(() ->
                CodeStyleManager.getInstance(getProject()).reformatText(
                        myFixture.getFile(),
                        ContainerUtil.newArrayList(myFixture.getFile().getTextRange())
                )
        );
        try {
            FileUtil.writeToFile(new File(getTestDataPath() + "/0-actual.hjson"), myFixture.getFile().getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        myFixture.checkResultByFile("0-expected.hjson");
    }

    private void commaTest(String prefix, BiConsumer<HJsonCodeStyleSettings, CommonCodeStyleSettings> configurator) {
        myFixture.configureByFile("comma/0-data.hjson");
//        CodeStyle.getLanguageSettings(myFixture.getFile()).SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
        HJsonCodeStyleSettings customSettings = CodeStyle.getCustomSettings(myFixture.getFile(), HJsonCodeStyleSettings.class);
        configurator.accept(customSettings,CodeStyle.getLanguageSettings(myFixture.getFile()));

        WriteCommandAction.writeCommandAction(getProject()).run(() ->
                CodeStyleManager.getInstance(getProject()).reformatText(
                        myFixture.getFile(),
                        ContainerUtil.newArrayList(myFixture.getFile().getTextRange())
                )
        );
        try {
            FileUtil.writeToFile(new File(getTestDataPath() + "/comma/actual/" +prefix+ ".hjson"), myFixture.getFile().getText());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        myFixture.checkResultByFile("comma/"+prefix+"-expected.hjson");
    }

    @Test
    public void testCommaRemoverWithoutAutoWrap() {
        commaTest("remove-all-without-wrap",(customSettings,it)->{
            customSettings.ARRAY_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
            customSettings.OBJECT_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
            customSettings
                    .trailingComma(CommaState.KEEP)
                    .commas(CommaState.REMOVE);
        });
    }
    @Test
    public void testCommaRemoverWithAutoWrap() {
        commaTest("remove-all-with-wrap",(customSettings,it)->{
//            customSettings.ARRAY_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
//            customSettings.OBJECT_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
            customSettings
                    .trailingComma(CommaState.KEEP)
                    .commas(CommaState.REMOVE);
        });
    }
    @Test
    public void testAddAllComma() {
        commaTest("add-all",(customSettings,it)->{
//            customSettings.ARRAY_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
//            customSettings.OBJECT_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
            customSettings
                    .trailingComma(CommaState.KEEP)
                    .commas(CommaState.ADD);
        });
    }
    @Test
    public void testKeepAllCommaRemoveTrailing() {
        commaTest("remove-trailing",(customSettings,it)->{
//            customSettings.ARRAY_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
//            customSettings.OBJECT_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
            customSettings
                    .trailingComma(CommaState.REMOVE)
                    .commas(CommaState.KEEP);
        });
    }
    @Test
    public void testKeepAllCommaAddTrailing() {
        commaTest("add-trailing",(customSettings,it)->{
//            customSettings.ARRAY_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
//            customSettings.OBJECT_WRAPPING = CommonCodeStyleSettings.DO_NOT_WRAP;
            customSettings
                    .trailingComma(CommaState.ADD)
                    .commas(CommaState.KEEP);
        });
    }


}