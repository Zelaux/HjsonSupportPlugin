package com.zelaux.hjson.no;

import com.intellij.psi.PsiFile;
import com.zelaux.hjson.HJsonParserTest;
import com.zelaux.hjson.psi.visitors.PrintVisitor;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class ParserPreview extends HJsonParserTest {
    @Override
    @Test
    public void testParsingTestData() {
        PsiFile file = parseFile("dummy.hjson", "[1,]");
        System.out.println(file.getFirstChild().getLastChild().getPrevSibling());
        System.out.println(toParseTreeText(file, false, true));
        try {
            PrintVisitor visitor = new PrintVisitor();
            file.accept(visitor);
            System.out.println(visitor.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Assert.fail();
    }
}
