package com.zelaux.hjson.parser;

import com.intellij.openapi.util.text.StringUtil;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import org.junit.Assert;
import org.junit.Test;

public class MultilineParserTest {
    @Test
    public void testMultilineParser() {
        String[][] cases = {

        };
        int offset = "field: ".length();
        for (int i = 0; i < cases.length; i++) {
            String[] testCase = cases[i];
            String actual = HJsonPsiImplUtils.getMultilineString(testCase[0], offset, testCase[0].substring(offset));
            Assert.assertEquals("multiline case["+i+"]",
                    StringUtil.escapeStringCharacters(testCase[1]),
                    StringUtil.escapeStringCharacters(actual));
        }
    }
}
