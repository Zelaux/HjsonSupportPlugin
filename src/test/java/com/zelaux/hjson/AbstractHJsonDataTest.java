package com.zelaux.hjson;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import com.zelaux.hjson.hjsonDataTests.PuzzlerTest;
import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AbstractHJsonDataTest extends ParsingTestCase {
    private static final Logger LOG = Logger.getInstance(PuzzlerTest.class);

    public AbstractHJsonDataTest(String dataPath) {
        super(dataPath, "hjson", new HJsonParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/hjsonData";
    }

    protected void runTest(String fileName, CaseConsumer consumer) throws IOException {
        String rawText = loadFile(fileName);
        myFile = createPsiFile(fileName, rawText);
        ensureNoErrorElements();
        HJsonObject object = PsiTreeUtil.getChildOfAnyType(myFile, HJsonObject.class);
        ToJsonPrintVisitor visitor = new ToJsonPrintVisitor();
        for (HJsonMember member : object.getMemberList()) {
            String testName = member.getName();

            List<HJsonValue> values = ((HJsonArray) member.getValue()).getValueList();
            String testCode = ((HJsonStringLiteral) values.get(0)).getValue();


            @Nullable
            String expectedResult = (values.get(1) instanceof HJsonNullLiteral ? null : ((HJsonStringLiteral) values.get(1)).getValue());

            if (values.size() > 2) {
                LOG.warn("Test " + getTestDataPath() + "/" + fileName + "#" + testName + " was ignored");
                consumer.consume(new HJsonCase(testName, testCode, expectedResult));
                continue;
            }

            myFile = createPsiFile(testName + ".hjson", testCode);

            myFile.accept(visitor);
            String json = visitor.getString();
            visitor.reset();
            if (expectedResult == null) {
                try {
                    ensureNoErrorElements();
                    consumer.consume(new HJsonCase(
                            testName, testCode, null, json
                    ));
                } catch (AssertionFailedError e) {
                    consumer.consume(new HJsonCase(
                            testName, testCode, null, null
                    ));
                }
            } else {
                try {
                    ensureNoErrorElements();
                    consumer.consume(new HJsonCase(
                            testName, testCode, expectedResult, json
                    ));
                } catch (AssertionFailedError e) {
                    consumer.consume(new HJsonCase(
                            testName, testCode, expectedResult, e.getMessage()
                    ));
                }
            }
        }
    }

    protected void checkAll(String fileName) throws IOException {
        StringBuilder actual=new StringBuilder();
        StringBuilder expected=new StringBuilder();
        runTest(fileName,test->{
            actual.append(test.name).append(": ");
            expected.append(test.name).append(": ");
            if(test.skipped){
                actual.append("SKIPPED");
                expected.append("SKIPPED");
            } else{
                actual.append(Objects.requireNonNullElse(test.actualJson, "NaN parsed"));
                expected.append(Objects.requireNonNullElse(test.expectedJson, "NaN parsed"));
            }
            actual.append("\n");
            expected.append("\n");
        });
        Assert.assertEquals(expected.toString(),actual.toString());
    }

    public interface CaseConsumer {
        void consume(HJsonCase hJsonCase);
    }

    public static class HJsonCase {
        public final String name;
        public final String code;
        @Nullable
        public final String expectedJson;
        @Nullable
        public final String actualJson;
        public final boolean skipped;

        public HJsonCase(String name, String code, @Nullable String expectedJson) {
            this.name = name;
            this.code = code;
            this.expectedJson = expectedJson;
            skipped = true;
            actualJson = null;
        }

        public HJsonCase(String name, String code, @Nullable String expectedJson, @Nullable String actualJson) {
            this.name = name;
            this.code = code;
            this.expectedJson = expectedJson;
            this.actualJson = actualJson;
            this.skipped = false;
        }
    }

    public void test() throws IOException {

    }
}
