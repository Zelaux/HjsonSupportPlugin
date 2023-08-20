package com.zelaux.hjson.psi.visitors;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.util.IndentStream;
import org.jetbrains.annotations.NotNull;

public class SimplifyPrintVisitor extends AbstractPrintVisitor {

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    final IndentStream stream = new IndentStream(super.stream, /*"\u0020\u0020"*/"");


    @Override
    public void visitLiteral(@NotNull HJsonLiteral o) {
        stream.print(o.getText());
    }

    @Override
    public void visitStringLiteral(@NotNull HJsonStringLiteral o) {
        String value = o.getValue();
        if (value.contains("\n")) {
            if(o.getParent() instanceof HJsonMemberValue){
                stream.println();
            }
            stream.println("'''");
            for (String line : value.split("\n")) {
                stream.println(line);
            }
            stream.print("'''");
        } else if (value.matches(".*\\s")) {
            stream.print(o.getText());
        } else {
            stream.print(value);
        }
    }

    @Override
    public void visitArray(@NotNull HJsonArray o) {
        stream.println("[");
        stream.increaseIndent();
        for (HJsonValue value : o.getValueList()) {
            value.accept(this);
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
        if (o.getParent() instanceof HJsonFile) {
            visitObject(o);
            return;
        }
        stream.println("{");
        stream.increaseIndent();
        visitObject(o);
        stream.decreaseIndent();
        stream.print("}");

    }

    @Override
    public void visitMultilineString(@NotNull HJsonMultilineString o) {

        if(o.getParent() instanceof HJsonMemberValue){
            stream.println();
        }
        stream.println("'''");
        for (String line : o.getLines()) {
            stream.println(line);
        }
        stream.print("'''");
    }

    @Override
    public void visitObject(@NotNull HJsonObject o) {
        for (HJsonMember member : o.getMemberList()) {
            member.accept(this);
            stream.println();
        }
    }

    @Override
    public void visitMember(@NotNull HJsonMember o) {

        String text = o.getName();

        if (text.matches(".*\\s.*")) {
            stream.print('"');
            stream.print(StringUtil.escapeStringCharacters(text));
            stream.print('"');
        } else {
            stream.print(text);
        }
        stream.print(": ");
        HJsonValue value = o.getValue();
        if (value != null) value.accept(this);
    }

    @Override
    public void reset() {
        super.reset();
        stream.reset();
    }
}
