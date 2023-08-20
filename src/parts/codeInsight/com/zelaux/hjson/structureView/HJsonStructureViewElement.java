package com.zelaux.hjson.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HJsonStructureViewElement  implements StructureViewTreeElement {
    private final HJsonElement myElement;

    public HJsonStructureViewElement(@NotNull HJsonElement element) {
        assert PsiTreeUtil.instanceOf(element, HJsonFile.class, HJsonMember.class, HJsonObject.class, HJsonArray.class);
        myElement = element;
    }

    @Override
    public HJsonElement getValue() {
        return myElement;
    }

    @Override
    public void navigate(boolean requestFocus) {
        myElement.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return myElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return myElement.canNavigateToSource();
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        final ItemPresentation presentation = myElement.getPresentation();
        assert presentation != null;
        return presentation;
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        HJsonElement value = null;
        if (myElement instanceof HJsonFile) {
            value = ((HJsonFile)myElement).getTopLevelValue();
        }
        else if (myElement instanceof HJsonMember) {
            value = ((HJsonMember)myElement).getValue();
        }
        else if (PsiTreeUtil.instanceOf(myElement, HJsonObject.class, HJsonArray.class)) {
            value = myElement;
        }
        if (value instanceof HJsonObject) {
            final HJsonObject object = ((HJsonObject)value);
            return ContainerUtil.map2Array(object.getMemberList(), TreeElement.class, HJsonStructureViewElement::new);
        }
        else if (value instanceof HJsonArray) {
            final HJsonArray array = (HJsonArray)value;
            final List<TreeElement> childObjects = ContainerUtil.mapNotNull(array.getValueList(), value1 -> {
                if (value1 instanceof HJsonObject && !((HJsonObject)value1).getMemberList().isEmpty()) {
                    return new HJsonStructureViewElement(value1);
                }
                else if (value1 instanceof HJsonArray && PsiTreeUtil.findChildOfType(value1, HJsonMember.class) != null) {
                    return new HJsonStructureViewElement(value1);
                }
                return null;
            });
            return childObjects.toArray(TreeElement.EMPTY_ARRAY);
        }
        return EMPTY_ARRAY;
    }
}
