package com.zelaux.hjson.psi.visitors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import com.zelaux.hjson.util.IndentStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PrintVisitor extends HJsonRecursiveElementVisitor {
    private final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    private final PrintStream rawStream = new PrintStream(byteOutputStream);
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    final IndentStream stream = new IndentStream(rawStream, "\u0020\u0020");
    public boolean arrayComma = true;
    public boolean objectComma = true;

/*    @Override
    public void visitElement(@NotNull PsiElement o) {
        stream.increaseIndent();
        o.acceptChildren(this);
        stream.decreaseIndent();
    }*/

    @Override
    public void visitLiteral(@NotNull HJsonLiteral o) {
        stream.print(o.getText());
    }

    @Override
    public void visitArray(@NotNull HJsonArray o) {
        stream.println("[");
        stream.increaseIndent();
        for (HJsonValue value : o.getValueList()) {
            value.accept(this);
            if (arrayComma && needComma(value)) {
                stream.print(",");
            }
            stream.println();
        }
        stream.decreaseIndent();
        stream.print("]");
    }

    @Override
    public void visitFile(@NotNull PsiFile file) {
        if (file instanceof HJsonFile) {
            for (PsiElement child : file.getChildren()) {
                if (child instanceof HJsonElement) {
                    child.accept(this);
                }
            }
        } else {
            super.visitFile(file);
        }
    }

    @Override
    public void visitObjectFull(@NotNull HJsonObjectFull o) {
        visitObject(o);

    }

    @Override
    public void visitMultilineString(@NotNull HJsonMultilineString o) {

        String[] split = o.getValue().split("\n");
        stream.println("'''");
        for (int i = 0; i < split.length; i++) {
            stream.println(split[i]);
        }
        stream.print("'''");
    }

    @Override
    public void visitObject(@NotNull HJsonObject o) {
        stream.println("{");
        stream.increaseIndent();
        for (HJsonMember member : o.getMemberList()) {
            member.accept(this);
            if (objectComma && needComma(member.getValue())) {
                stream.print(',');
            }
            stream.println();
        }
        stream.decreaseIndent();
        stream.print("}");
    }

    private static boolean needComma(HJsonValue value) {
        return !(value instanceof HJsonQuoteLessString);
    }

    @Override
    public void visitMember(@NotNull HJsonMember o) {

        o.getMemberName().accept(this);
        stream.print(": ");
        o.getValue().accept(this);
    }

    public String getString() {
        return byteOutputStream.toString();
    }

    public void reset() {
        byteOutputStream.reset();
        stream.reset();
    }
}
