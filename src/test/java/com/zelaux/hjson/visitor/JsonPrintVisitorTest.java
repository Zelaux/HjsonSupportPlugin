package com.zelaux.hjson.visitor;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.ParsingTestCase;
import com.zelaux.hjson.HJsonParserDefinition;
import com.zelaux.hjson.psi.impl.HJsonFileImpl;
import com.zelaux.hjson.psi.visitors.AbstractPrintVisitor;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("JUnitMixedFramework")
public class JsonPrintVisitorTest extends ParsingTestCase {

    public JsonPrintVisitorTest() {
        super("", "hjson", new HJsonParserDefinition());
    }

    @Test
    public void test() {
        try {
            AbstractPrintVisitor visitor = new ToJsonPrintVisitor();
            HJsonFileImpl file = (HJsonFileImpl) parseFile("data.hjson", loadFile("data.hjson"));
            ensureNoErrorElements();
            file.accept(visitor);
            String visitorString = visitor.getString().replace("\r\n", "\n");
            visitor.reset();
            //src/test/testData/visitor/data.hjson
            //src\test\testData\visitor\src\test\testData\visitor\data.hjson
            PsiFile psiFile = parseFile("Dummy.hjson", visitorString);
            myFile=psiFile;
            psiFile.accept(visitor);
            FileUtil.writeToFile(new File(myFullDataPath, "actual.hjson"), visitorString);
            doCheckResult(myFullDataPath, "expected.hjson", visitorString);
            Assert.assertEquals(visitorString, visitor.getString().replace("\r\n", "\n"));
            toParseTreeText(file, true, false);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/visitor/json";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}