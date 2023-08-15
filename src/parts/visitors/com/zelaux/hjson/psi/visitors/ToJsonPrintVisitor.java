package com.zelaux.hjson.psi.visitors;

import com.intellij.openapi.util.text.StringUtil;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToJsonPrintVisitor extends AbstractPrintVisitor {


    @Override
    public void visitLiteral(@NotNull HJsonLiteral o) {
//        o.accept(this);
        stream.print(o.getText());
    }

    @Override
    public void visitMember(@NotNull HJsonMember o) {
        String name = HJsonPsiUtil.stripQuotes(o.getName());
        stream.print('"');
        stream.print(StringUtil.escapeStringCharacters(name));
        stream.print('"');
        stream.print(':');
        stream.print(' ');
        HJsonMemberValue value = o.getMemberValue();
       if(value!=null) value.accept(this);
    }

    @Override
    public void visitObject(@NotNull HJsonObject o) {
        stream.print('{');
        List<HJsonMember> list = o.getMemberList();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).accept(this);
            if(i+1<list.size()){
                stream.print(", ");
            }
        }
        stream.print('}');
    }

    @Override
    public void visitArray(@NotNull HJsonArray o) {
        stream.print('[');
        List<HJsonValue> list = o.getValueList();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).accept(this);
            if(i+1<list.size()){
                stream.print(", ");
            }
        }
        stream.print(']');
    }

    @Override
    public void visitObjectFull(@NotNull HJsonObjectFull o) {
        visitObject(o);
    }

    @Override
    public void visitMultilineString(@NotNull HJsonMultilineString o) {
        stream.print('"');
        stream.print(StringUtil.escapeStringCharacters(o.getValue()));
        stream.print('"');
    }

    @Override
    public void visitQuotelessString(@NotNull HJsonQuotelessString o) {
        stream.print('"');
        stream.print(o.getValue());
        stream.print('"');
    }

}
