package com.zelaux.hjson.psi.visitors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import com.zelaux.hjson.utils.IndentStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PrintVisitor extends HJsonRecursiveElementVisitor {
    private final ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    private final PrintStream rawStream = new PrintStream(byteOutputStream);
    final IndentStream stream = new IndentStream(rawStream);
    public boolean arrayComma = true;
    public boolean objectComma = true;

    @Override
    public void visitElement(@NotNull PsiElement o) {
        stream.increaseIndent();
        o.acceptChildren(this);
        stream.decreaseIndent();
    }

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
            if (arrayComma) stream.print(',');
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
        HJsonObject object = o.getObject();
        if (object != null) {
            object.accept(this);
        } else {
            stream.print("{}");
        }

    }

    @Override
    public void visitMultilineString(@NotNull HJsonMultilineString o) {

        String[] split = o.getValue().split("\n");

        for (int i = 0; i < split.length - 1; i++) {
            stream.println(split[i]);
        }
        if(split.length>0){
            stream.print(split[split.length-1]);
        }
    }

    @Override
    public void visitObject(@NotNull HJsonObject o) {
        stream.println("{");
        if (o != null) {
            stream.increaseIndent();
            for (HJsonMember member : o.getMemberList()) {
                member.accept(this);
                if (objectComma) stream.print(',');
                stream.println();
            }
            stream.decreaseIndent();
        }
        stream.print("}");
    }

    @Override
    public void visitMember(@NotNull HJsonMember o) {

        o.getMemberName().getNameElement().accept(this);
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
