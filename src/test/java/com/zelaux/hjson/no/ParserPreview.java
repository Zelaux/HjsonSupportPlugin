package com.zelaux.hjson.no;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.HJsonParserTest;
import com.zelaux.hjson.psi.visitors.AbstractPrintVisitor;
import com.zelaux.hjson.psi.visitors.SimplifyPrintVisitor;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class ParserPreview extends HJsonParserTest {
    @Override
    @Test
    public void testParsingTestData() {
        PsiFile file = createPsiFile("dummy.hjson", "name d: hh\ndisplayName: HotH\nauthor: RedHotFox aka RePoweReD\ndescription: Three mods here\nversion: 0.1\nminGameVersion: 145\nmain: halo.Halo\n");
        myFile=file;
        System.out.println(toParseTreeText(file, false, true));
        ensureNoErrorElements();
//        System.out.println(file.getFirstChild().getLastChild().getPrevSibling());
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
