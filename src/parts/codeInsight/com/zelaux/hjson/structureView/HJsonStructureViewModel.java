package com.zelaux.hjson.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.zelaux.hjson.psi.HJsonArray;
import com.zelaux.hjson.psi.HJsonFile;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HJsonStructureViewModel  extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {

    public HJsonStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor) {
        super(psiFile, editor, new HJsonStructureViewElement((HJsonFile)psiFile));
        withSuitableClasses(HJsonFile.class, HJsonMember.class, HJsonObject.class, HJsonArray.class);
        withSorters(Sorter.ALPHA_SORTER);
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return false;
    }

}
