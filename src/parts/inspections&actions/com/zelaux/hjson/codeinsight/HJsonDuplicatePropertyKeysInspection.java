package com.zelaux.hjson.codeinsight;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.icons.AllIcons;
import com.intellij.json.JsonBundle;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import com.jetbrains.jsonSchema.ide.JsonSchemaService;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.psi.HJsonElementVisitor;
import com.zelaux.hjson.psi.HJsonMember;
import com.zelaux.hjson.psi.HJsonObject;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HJsonDuplicatePropertyKeysInspection  extends LocalInspectionTool {
    private static final String COMMENT = "$comment";

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        boolean isSchemaFile = JsonSchemaService.isSchemaFile(holder.getFile());
        return new HJsonElementVisitor() {
            @Override
            public void visitObject(@NotNull HJsonObject o) {
                final MultiMap<String, PsiElement> keys = new MultiMap<>();
                for (HJsonMember property : o.getMemberList()) {
                    keys.putValue(property.getName(), property.getMemberName());
                }
                for (Map.Entry<String, Collection<PsiElement>> entry : keys.entrySet()) {
                    final Collection<PsiElement> sameNamedKeys = entry.getValue();
                    final String entryKey = entry.getKey();
                    if (sameNamedKeys.size() > 1 && (!isSchemaFile || !COMMENT.equalsIgnoreCase(entryKey))) {
                        for (PsiElement element : sameNamedKeys) {
                            holder.registerProblem(element, JsonBundle.message("inspection.duplicate.keys.msg.duplicate.keys", entryKey),
                                    new NavigateToDuplicatesFix(sameNamedKeys, element, entryKey));
                        }
                    }
                }
            }
        };
    }

    private static final class NavigateToDuplicatesFix extends LocalQuickFixAndIntentionActionOnPsiElement {
        @NotNull private final Collection<SmartPsiElementPointer> mySameNamedKeys;
        @NotNull private final String myEntryKey;

        private NavigateToDuplicatesFix(@NotNull Collection<PsiElement> sameNamedKeys, @NotNull PsiElement element, @NotNull String entryKey) {
            super(element);
            mySameNamedKeys = ContainerUtil.map(sameNamedKeys, SmartPointerManager::createPointer);
            myEntryKey = entryKey;
        }

        @NotNull
        @Override
        public String getText() {
            return JsonBundle.message("navigate.to.duplicates");
        }

        @Nls(capitalization = Nls.Capitalization.Sentence)
        @NotNull
        @Override
        public String getFamilyName() {
            return getText();
        }

        @Override
        public void invoke(@NotNull Project project,
                           @NotNull PsiFile file,
                           @Nullable Editor editor,
                           @NotNull PsiElement startElement,
                           @NotNull PsiElement endElement) {
            if (editor == null) return;

            if (mySameNamedKeys.size() == 2) {
                final Iterator<SmartPsiElementPointer> iterator = mySameNamedKeys.iterator();
                final PsiElement next = iterator.next().getElement();
                PsiElement toNavigate = next != startElement ? next : iterator.next().getElement();
                if (toNavigate == null) return;
                navigateTo(editor, toNavigate);
            }
            else {
                final List<PsiElement> allElements =
                        mySameNamedKeys.stream().map(SmartPsiElementPointer::getElement).filter(k -> k != startElement).collect(Collectors.toList());
                JBPopupFactory.getInstance().createListPopup(
                        new BaseListPopupStep<>(JsonBundle.message("navigate.to.duplicates.header", myEntryKey), allElements) {
                            @NotNull
                            @Override
                            public Icon getIconFor(PsiElement aValue) {
                                return AllIcons.Nodes.Property;
                            }

                            @NotNull
                            @Override
                            public String getTextFor(PsiElement value) {
                                return JsonBundle
                                        .message("navigate.to.duplicates.desc", myEntryKey, editor.getDocument().getLineNumber(value.getTextOffset()));
                            }

                            @Override
                            public int getDefaultOptionIndex() {
                                return 0;
                            }

                            @Nullable
                            @Override
                            public PopupStep onChosen(PsiElement selectedValue, boolean finalChoice) {
                                navigateTo(editor, selectedValue);
                                return PopupStep.FINAL_CHOICE;
                            }

                            @Override
                            public boolean isSpeedSearchEnabled() {
                                return true;
                            }
                        }).showInBestPositionFor(editor);
            }
        }

        private static void navigateTo(@NotNull Editor editor, @NotNull PsiElement toNavigate) {
            editor.getCaretModel().moveToOffset(toNavigate.getTextOffset());
            editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        }
    }
}
