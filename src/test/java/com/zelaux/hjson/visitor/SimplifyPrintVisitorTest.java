package com.zelaux.hjson.visitor;

import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.ParsingTestUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.formatter.HJsonCodeStyleSettings;
import com.zelaux.hjson.formatter.style.CommaState;
import com.zelaux.hjson.formatter.style.PropertyAlignment;
import com.zelaux.hjson.psi.visitors.AbstractPrintVisitor;
import com.zelaux.hjson.psi.visitors.SimplifyPrintVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("JUnitMixedFramework")
public class SimplifyPrintVisitorTest extends LightJavaCodeInsightFixtureTestCase {

    @Test
    public void test() {
        try {
            AbstractPrintVisitor visitor = new SimplifyPrintVisitor();
            myFixture.configureByFile("data.hjson");
            ensureNoErrorElements();
            accept(visitor);
            String actualText = visitor.getString().replace("\r\n", "\n");
            //src/test/testData/visitor/data.hjson
            //src\test\testData\visitor\src\test\testData\visitor\data.hjson
            myFixture.configureByText("dummy.hjson", actualText);
//            HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(myFixture.getFile(), HJsonCodeStyleSettings.class);
          /*  CommonCodeStyleSettings languageSettings = CodeStyle.getLanguageSettings(myFixture.getFile());
            languageSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
            languageSettings.KEEP_BLANK_LINES_IN_CODE=0;*/
//        CodeStyle.getLanguageSettings(myFixture.getFile()).KEEP_BLANK_LINES_IN_CODE = 2;
           /* HJsonCodeStyleSettings settings = CodeStyle.getCustomSettings(myFixture.getFile(), HJsonCodeStyleSettings.class);
            settings
                    .trailingComma(CommaState.KEEP)
                    .commas(CommaState.KEEP)
            ;*/
//            settings.PROPERTY_ALIGNMENT= PropertyAlignment.CO.getId();
            ;
            format();
            System.out.println(actualText);
            actualText = myFixture.getFile().getText();
            ensureNoErrorElements();
            visitor.reset();
            accept(visitor);
            FileUtil.writeToFile(new File(getTestDataPath(), "actual.hjson"), actualText);

            UsefulTestCase.assertSameLinesWithFile(getTestDataPath() + "/expected.hjson", actualText);
            myFixture.configureByText("dummy.hjson",visitor.getString());
            System.out.println("ddddd");
            System.out.println(visitor.getString());
            format();
            System.out.println(actualText);
            Assert.assertEquals(actualText, myFixture.getFile().getText());
            DebugUtil.psiToString(myFixture.getFile(), true, true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void format() {
        WriteCommandAction.writeCommandAction(getProject()).run(() ->
                CodeStyleManager.getInstance(getProject()).reformatText(
                        myFixture.getFile(),
                        ContainerUtil.newArrayList(myFixture.getFile().getTextRange())
                )
        );
    }

    private void accept(AbstractPrintVisitor visitor) {
        myFixture.getFile().accept(visitor);
    }

    private void ensureNoErrorElements() {
        ParsingTestUtil.ensureNoErrorElements(myFixture.getFile());
    }

    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/visitor/simplify";
    }

}