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
        PsiFile file = parseFile("dummy.hjson", "{\"property\": 11, \"array\": [12, 34, 45, 567], \"object\": {\"one\": 0, \"two\": {\"hmm\\n\": \"it\"}, \"three\": 2}, \"version\": 0.0, \"boolean\": false, \"boolean2\": true, \"number\": 013213e-1, \"strings\": [\"double_quoted\", 'single_quoted', \"no qouted\"], \"name\": {\"obj\": \"it\"}, \"field\": 123, \"array\": [123, \"bvad\", \"dawd,\", 'ddawd', \"multiline1\\nmultiline2\\nmultiline3\\nmultiline4\"]}");
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
