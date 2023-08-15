package com.zelaux.hjson.psi.visitors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import com.zelaux.hjson.util.IndentStream;
import com.zelaux.hjson.util.LegalPrintStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public abstract class AbstractPrintVisitor extends HJsonRecursiveElementVisitor {
    private final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    protected final LegalPrintStream stream = new LegalPrintStream(byteOutputStream);
    @SuppressWarnings("UnnecessaryUnicodeEscape")


    public String getString() {
        return byteOutputStream.toString().replace("\r\n","\n");
    }

    public void reset() {
        byteOutputStream.reset();
    }
}
