package com.zelaux.hjson.hjsonDataTests;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.zelaux.hjson.HJsonParserDefinition;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("JUnitMixedFramework")
public class PuzzlerTest extends ParsingTestCase {
    private    static final Logger LOG=Logger.getInstance(PuzzlerTest.class);
    public PuzzlerTest() {
        super("", "hjson", new HJsonParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/hjsonData";
    }

    @Test
    public void test() throws IOException {
        String rawText = loadFile("puzzlers.hjson");
        myFile = createPsiFile("puzzlers.hjson", rawText);
        ensureNoErrorElements();
        HJsonObject object = PsiTreeUtil.getChildOfAnyType(myFile, HJsonObject.class);
        ToJsonPrintVisitor visitor = new ToJsonPrintVisitor();
        StringBuilder actualBuilder = new StringBuilder();
        StringBuilder expectedBuilder = new StringBuilder();
        StringBuilder puzzlerTxt = new StringBuilder();
        for (HJsonMember member : object.getMemberList()) {
            String puzzlerName = member.getName();
            actualBuilder.append("HJson Puzzler#").append(puzzlerName).append(" '");
            expectedBuilder.append("HJson Puzzler#").append(puzzlerName).append(" '");
            puzzlerTxt.append("HJson Puzzler#").append(puzzlerName).append("\n");
            List<HJsonValue> values = ((HJsonArray) member.getValue()).getValueList();
            String puzzlerText = ((HJsonStringLiteral) values.get(0)).getValue();



            @Nullable
            String expectedResult = (values.get(1) instanceof HJsonNullLiteral ? null : ((HJsonStringLiteral) values.get(1)).getValue());

            puzzlerTxt.append("```\n");
            puzzlerTxt.append(puzzlerText);
            puzzlerTxt.append("\n```\nAnswer: \n");
            if(expectedResult==null){
                puzzlerTxt.append("Do not compile");
            } else{
                puzzlerTxt.append("```json\n");
                puzzlerTxt.append(expectedResult);
                puzzlerTxt.append("\n```");
            }
            puzzlerTxt.append("\n\n");
            if(values.size()>2){
                LOG.warn("HJson Puzzler#"+puzzlerName+" was ignored");
                actualBuilder.append("IGNORE'\n");
                expectedBuilder.append("IGNORE'\n");
                continue;
            }

            myFile = createPsiFile("puzzler" + puzzlerName + ".hjson", puzzlerText);

            myFile.accept(visitor);
            String json = visitor.getString();
            visitor.reset();
            if (expectedResult == null) {
                try {
                    ensureNoErrorElements();
                    actualBuilder.append("Puzzler was parsed |").append(json).append("|'").append("'\n");
                } catch (AssertionFailedError e) {
                    actualBuilder.append("NaN parsed").append("'\n");
                }
                expectedBuilder.append("NaN parsed").append("'\n");
            } else {
                try {
                    ensureNoErrorElements();
                    actualBuilder.append(json).append("'\n");
                } catch (AssertionFailedError e) {
                    actualBuilder.append(e.getMessage()).append("'\n");
                }
                expectedBuilder.append(expectedResult).append("'\n");
            }
        }
        FileUtil.writeToFile(new File(getTestDataPath()+"/puzzlers.txt"),puzzlerTxt.toString());
        Assert.assertEquals(expectedBuilder.toString(), actualBuilder.toString());
    }
}
