package com.zelaux.hjson.actions;

import com.intellij.json.psi.JsonElementGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.HJsonFactory;
import com.zelaux.hjson.psi.visitors.PrintVisitor;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;
import org.jetbrains.annotations.NotNull;

public class ToJsonCodeAction extends MyEditorAction {
    protected ToJsonCodeAction() {
        super(null);
        MyEditorWriteActionHandler<Object> handler = new MyEditorWriteActionHandler<>(this) {
            @Override
            protected void executeWriteAction(Editor editor, DataContext dataContext, Object additionalParameter) {
                Project project = editor.getProject();
                assert project != null;
                Document document = editor.getDocument();
                PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
                ToJsonPrintVisitor visitor = new ToJsonPrintVisitor();
                assert file != null;
                file.accept(visitor);
                PsiFile newFile = new JsonElementGenerator(project).createDummyFile(visitor.getString());

                CodeStyleManager.getInstance(project).reformatText(
                        newFile,
                        ContainerUtil.newArrayList(newFile.getTextRange())
                );
                document.setText(newFile.getText());
//                file.replace();
            }

            @Override
            public boolean isEnabled(Editor editor, DataContext dataContext) {
                Project project = editor.getProject();
                if (project == null) return false;
                PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
                return file != null && file.getLanguage() == HJsonLanguage.INSTANCE;
//                return super.isEnabledForCaret(editor, caret, dataContext);
            }
        };
        this.setupHandler(handler);
    }


}
