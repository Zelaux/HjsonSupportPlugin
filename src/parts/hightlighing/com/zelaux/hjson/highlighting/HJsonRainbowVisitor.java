package com.zelaux.hjson.highlighting;

import com.intellij.codeInsight.daemon.RainbowVisitor;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.json.highlighting.JsonSyntaxHighlighterFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HJsonRainbowVisitor extends RainbowVisitor {


    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof HJsonFile;
    }

    @Override
    public void visit(@NotNull PsiElement element) {
        if (element instanceof HJsonMember) {
            PsiFile file = element.getContainingFile();
            String fileName = file.getName();

            String name = ((HJsonMember)element).getName();
            addInfo(getInfo(file, ((HJsonMember)element).getMemberName(), name, JsonSyntaxHighlighterFactory.JSON_PROPERTY_KEY));
            HJsonValue value = ((HJsonMember)element).getValue();
            if (value instanceof HJsonObject) {
                addInfo(getInfo(file, value.getFirstChild(), name, JsonSyntaxHighlighterFactory.JSON_BRACES));
                addInfo(getInfo(file, value.getLastChild(), name, JsonSyntaxHighlighterFactory.JSON_BRACES));
            }
            else if (value instanceof HJsonArray) {
                addInfo(getInfo(file, value.getFirstChild(), name, JsonSyntaxHighlighterFactory.JSON_BRACKETS));
                addInfo(getInfo(file, value.getLastChild(), name, JsonSyntaxHighlighterFactory.JSON_BRACKETS));
                for (HJsonValue jsonValue : ((HJsonArray)value).getValueList()) {
                    addSimpleValueInfo(name, file, jsonValue);
                }
            }
            else {
                addSimpleValueInfo(name, file, value);
            }
        }
    }

    private void addSimpleValueInfo(String name, PsiFile file, HJsonValue value) {
        if (value instanceof HJsonStringLiteral) {
            addInfo(getInfo(file, value, name, JsonSyntaxHighlighterFactory.JSON_STRING));
        }
        else if (value instanceof HJsonNumberLiteral) {
            addInfo(getInfo(file, value, name, JsonSyntaxHighlighterFactory.JSON_NUMBER));
        }
        else if (value instanceof HJsonLiteral) {
            addInfo(getInfo(file, value, name, JsonSyntaxHighlighterFactory.JSON_KEYWORD));
        }
    }

    @NotNull
    @Override
    public HighlightVisitor clone() {
        return new HJsonRainbowVisitor();
    }
}
