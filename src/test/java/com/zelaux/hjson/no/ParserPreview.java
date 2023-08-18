package com.zelaux.hjson.no;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.parser.HJsonParserTest;
import com.zelaux.hjson.psi.visitors.AbstractPrintVisitor;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.IOException;

@SuppressWarnings("NewClassNamingConvention")
public class ParserPreview extends HJsonParserTest {
    static class LoggerFactory implements Logger.Factory {

        @Override
        public @NotNull Logger getLoggerInstance(@NotNull String category) {
            return new Logger() {
                @Override
                public boolean isDebugEnabled() {
                    return false;
                }

                @Override
                public void debug(String message) {

                }

                @Override
                public void debug(@Nullable Throwable t) {

                }

                @Override
                public void debug(String message, @Nullable Throwable t) {

                }

                @Override
                public void info(String message) {

                }

                @Override
                public void info(String message, @Nullable Throwable t) {

                }

                @Override
                public void warn(String message, @Nullable Throwable t) {

                }

                @Override
                public void error(String message, @Nullable Throwable t, String @NotNull ... details) {
                    int i=0;
                }

                @Override
                public void setLevel(@NotNull Level level) {

                }
            };
        }
    }
    @Override
    @Test
    public void testParsingTestData() {

//        Logger.setFactory(LoggerFactory.class);
        String myText = null;
        try {
            myText =loadFileDefault("src/test/testData/preview","parser.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PsiFile file = createPsiFile("dummy.hjson",
                myText
                );
        myFile=file;
//        com.intellij.diagnostic.PluginProblemReporter
//        System.out.println(ApplicationManager.getApplication().getService(PluginProblemReporter.class));
//        System.out.println(toParseTreeText(file, false, true));
        ensureNoErrorElements();
//        System.out.println(file.getFirstChild().getLastChild().getPrevSibling());
        try {
            AbstractPrintVisitor visitor = new ToJsonPrintVisitor();
            file.accept(visitor);
//            System.out.println(visitor.getString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Assert.fail();
    }
}
