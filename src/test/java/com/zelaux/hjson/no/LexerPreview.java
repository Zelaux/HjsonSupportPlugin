package com.zelaux.hjson.no;

import com.intellij.lang.impl.TokenSequence;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.FlexLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson._HJsonLexer;
import com.zelaux.hjson.lexer.DelegateLexer;
import com.zelaux.hjson.lexer.HJsonFlexLexer;
import com.zelaux.hjson.lexer.HJsonLexer;
import com.zelaux.hjson.lexer.WrappedHJsonLexer;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"UnstableApiUsage", "NewClassNamingConvention"})
public class LexerPreview {

    @Test
    public void test() throws IOException {
        Lexer lexer = lexer();
//        System.out.println();
        String myText = FileUtil.loadFile(new File("src/test/testData/preview/lexer.txt"))
                .replace("\r\n","\n")
                ;
        lexer.start(myText,0,myText.length(),2);
        while (lexer.getTokenType()!=null){
            IElementType type = lexer.getTokenType();
            int tokenStart = lexer.getTokenStart();
            int tokenEnd = lexer.getTokenEnd();
//            System.out.print(type);
            int i1 = myText.indexOf('\n', tokenStart);
//            if (i1 >= 0 && i1 < tokenEnd) System.out.println();
            String original = myText.substring(tokenStart, tokenEnd);
            String replace = StringUtil.escapeStringCharacters(original);
//            System.out.print(type + "(" + replace + ")\n");
            if (!original.equals(replace)) {
//                System.out.println();
            }
            lexer.advance();
        }
//        Assert.fail();
    }

    @NotNull
    private static FlexAdapter lexer() {
        return new HJsonLexer();
    }

    @NotNull
    private static FlexLexer flexer() {
        return new HJsonFlexLexer(new WrappedHJsonLexer());
    }/*@NotNull
    private static _HJsonLexer flexer() {
        return new _HJsonLexer() {
            @Override
            public void reset(CharSequence buffer, int start, int end, int initialState) {
                super.reset(buffer, start, end, initialState == 0 ? IN_OBJECT : initialState);
            }

            @Override
            public IElementType advance() throws IOException {
                IElementType advance = super.advance();
                if (advance == HJsonElementTypes.QUOTELESS_STRING_TOKEN) System.out.println("STATE " + yystate());
                return advance;
            }
        };
    }*/
}
