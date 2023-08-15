package com.zelaux.hjson.no;

import com.intellij.psi.PsiFile;
import com.zelaux.hjson.HJsonParserTest;
import com.zelaux.hjson.psi.visitors.AbstractPrintVisitor;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class ParserPreview extends HJsonParserTest {
    @Override
    @Test
    public void testParsingTestData() {
        PsiFile file = parseFile("dummy.hjson", "it it:it");
//        System.out.println(file.getFirstChild().getLastChild().getPrevSibling());
        System.out.println(toParseTreeText(file, false, true));
        try {
            AbstractPrintVisitor visitor = new ToJsonPrintVisitor();
            file.accept(visitor);
            System.out.println(visitor.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Assert.fail();
    }
}
