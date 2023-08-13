package com.zelaux.hjson.breadcrumbs;


import com.intellij.lang.Language;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import com.jetbrains.jsonSchema.impl.JsonSchemaDocumentationProvider;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonMemberName;
import com.zelaux.hjson.util.HJsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HJsonBreadcrumbsProvider implements BreadcrumbsProvider {
    private static final Language[] LANGUAGES = new Language[]{HJsonLanguage.INSTANCE};

    @Override
    public Language[] getLanguages() {
        return LANGUAGES;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement e) {
        return e instanceof HJsonMember ||  HJsonUtil.isArrayElement(e);
    }

    @NotNull
    @Override
    public String getElementInfo(@NotNull PsiElement e) {
        if (e instanceof HJsonMember) {
            return ((HJsonMember)e).getName();
        } else if (HJsonUtil.isArrayElement(e)) {
            int i = HJsonUtil.getArrayIndexOfItem(e);
            if (i != -1) return String.valueOf(i);
        }
        throw new AssertionError("Breadcrumbs can be extracted only from HJsomMember elements or HJsonArray child items");
    }

    @Nullable
    @Override
    public String getElementTooltip(@NotNull PsiElement e) {
        return null;//JsonSchemaDocumentationProvider.findSchemaAndGenerateDoc(e, null, true, null);
    }

    /*@NotNull TODO
    @Override
    public List<? extends Action> getContextActions(@NotNull PsiElement element) {
        HJsonQualifiedNameKind[] values = JsonQualifiedNameKind.values();
        List<Action> actions = new ArrayList<>(values.length);
        for (JsonQualifiedNameKind kind: values) {
            actions.add(new AbstractAction(JsonBundle.message("json.copy.to.clipboard", kind.toString())) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CopyPasteManager.getInstance().setContents(new StringSelection(JsonQualifiedNameProvider.generateQualifiedName(element, kind)));
                }
            });
        }
        return actions;
    }*/
}
