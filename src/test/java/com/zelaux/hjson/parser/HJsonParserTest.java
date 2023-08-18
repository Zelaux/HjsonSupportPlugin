package com.zelaux.hjson.parser;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.ParsingTestCase;
import com.zelaux.hjson.HJsonParserDefinition;
import com.zelaux.hjson.psi.HJsonElement;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonObject;
import com.zelaux.hjson.psi.impl.HJsonFileImpl;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import com.zelaux.hjson.psi.visitors.PrintVisitor;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("JUnitMixedFramework")
public class HJsonParserTest extends ParsingTestCase {

    public HJsonParserTest() {
        super("", "hjson", new HJsonParserDefinition());
    }

    @Test
    public void testParsingTestData() {
        doTest(true,true);
    }@Override
    protected void doTest(boolean checkResult, boolean ensureNoErrorElements) {
        String name = getTestName();
        try {
            String text = loadFile(name + "." + myFileExt);
            myFile = createPsiFile(name, text);
            ensureNoErrorElements();
            parseFile(name, text);
            if (checkResult) {
                checkResult(name, myFile);
                if (ensureNoErrorElements) {
                    ensureNoErrorElements();
                }
            }
            else {
                toParseTreeText(myFile, skipSpaces(), includeRanges());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected boolean checkAllPsiRoots() {
        return false;
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