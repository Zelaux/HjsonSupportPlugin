package com.zelaux.hjson.psi.visitors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import com.zelaux.hjson.util.IndentStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class JsonFormattedPrintVisitor extends PrintVisitor {
    {
        arrayComma=true;
        objectComma=true;
    }

    @Override
    public void visitLiteral(@NotNull HJsonLiteral o) {
//        o.accept(this);
        stream.print(o.getText());
    }

   /* @Override
    public void visitMemberName(@NotNull HJsonMemberName o) {

        String text = o.getText();
        boolean escaped = (text.startsWith("'") && text.endsWith("'")) || (text.startsWith("\"") && text.endsWith("\""));
        if(!escaped)stream.print('"');
        stream.print(text);
        if(!escaped)stream.print('"');
    }
*/




    @Override
    public void visitMultilineString(@NotNull HJsonMultilineString o) {
        stream.print('"');
        stream.print(o.getValue());
        stream.print('"');
    }

    @Override
    public void visitQuotelessString(@NotNull HJsonQuotelessString o) {
        stream.print('"');
        stream.print(o.getValue());
        stream.print('"');
    }

    @Override
    public void reset() {
        super.reset();
        arrayComma=objectComma=true;
    }
}
