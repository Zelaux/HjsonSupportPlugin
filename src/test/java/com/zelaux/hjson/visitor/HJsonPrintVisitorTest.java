package com.zelaux.hjson.visitor;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.zelaux.hjson.HJsonParserDefinition;
import com.zelaux.hjson.psi.impl.HJsonFileImpl;
import com.zelaux.hjson.psi.visitors.PrintVisitor;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("JUnitMixedFramework")
public class HJsonPrintVisitorTest extends ParsingTestCase {

    public HJsonPrintVisitorTest() {
        super("", "hjson", new HJsonParserDefinition());
    }

    @Test
    public void testPrintVisitor() {
        String name = getTestName();
        try {

            PrintVisitor visitor = new PrintVisitor();
            HJsonFileImpl file = (HJsonFileImpl) parseFile(name, loadFile(name + "." + myFileExt));
            ensureNoErrorElements();
            file.accept(visitor);
            String visitorString = visitor.getString().replace("\r\n", "\n");
            visitor.reset();
            parseFile("Dummy.hjson", visitorString).accept(visitor);
            FileUtil.writeToFile(new File(myFullDataPath, "__visitorString.hjson"), visitorString);
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
        return "src/test/testData";
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