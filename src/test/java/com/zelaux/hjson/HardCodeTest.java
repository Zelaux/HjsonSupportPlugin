package com.zelaux.hjson;

import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.visitors.PrintVisitor;
import org.junit.Assert;
import org.junit.Test;

public class HardCodeTest extends HJsonParserTest{
    @Override
    @Test
    public void testParsingTestData() {
        PsiFile file = parseFile("dummy.hjson", "it1: it2\n: it4");
        System.out.println(toParseTreeText(file, false, true));
        try {
            PrintVisitor visitor = new PrintVisitor();
            file.accept(visitor);
            System.out.println(visitor.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.fail();
    }
}
