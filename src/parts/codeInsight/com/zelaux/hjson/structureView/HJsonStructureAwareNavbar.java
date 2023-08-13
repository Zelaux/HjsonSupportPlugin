package com.zelaux.hjson.structureView;

import com.intellij.icons.AllIcons;
import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension;
import com.intellij.lang.Language;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.util.HJsonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HJsonStructureAwareNavbar extends StructureAwareNavBarModelExtension {
    @NotNull
    @Override
    protected Language getLanguage() {
        return HJsonLanguage.INSTANCE;
    }

    @Override
    public @Nullable String getPresentableText(Object __object) {
        if (!(__object instanceof HJsonElement)) return null;
        HJsonElement object = (HJsonElement) __object;
        if (object instanceof HJsonFile) {
            return ((HJsonFile) object).getName();
        }
        if (object instanceof HJsonMember) {
            return ((HJsonMember) object).getName();
        }
        if (HJsonUtil.isArrayElement(object)) {
            return "[" + HJsonUtil.getArrayIndexOfItem(object) + "]";
        }
        if (object instanceof HJsonStringLiteral) {
            String value = ((HJsonStringLiteral) object).getValue();
            int i = value.indexOf('\n');
            if (i != -1) return value.substring(0, i) + "...";
            return value;
        } else if (object instanceof HJsonNullLiteral) {
            return "null";
        } else if (object instanceof HJsonNumberLiteral) {
            return String.valueOf(((HJsonNumberLiteral) object).getValue());
        } else if (object instanceof HJsonBooleanLiteral) {
            return String.valueOf(((HJsonBooleanLiteral) object).getValue());
        }

        return null;
    }

    @Override
    @Nullable
    public Icon getIcon(Object object) {
        if (object instanceof HJsonMember) {
            return AllIcons.Nodes.Property;
        }
        if (object instanceof HJsonObject) {
            return AllIcons.Json.Object;
        }
        if (object instanceof HJsonArray) {
            return AllIcons.Json.Array;
        }

        return null;
    }

}