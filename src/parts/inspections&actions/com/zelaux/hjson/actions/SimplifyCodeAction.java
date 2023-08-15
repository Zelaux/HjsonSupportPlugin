package com.zelaux.hjson.actions;

import com.intellij.json.psi.JsonElementVisitor;
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
import com.zelaux.hjson.psi.visitors.AbstractPrintVisitor;
import com.zelaux.hjson.psi.visitors.PrintVisitor;
import com.zelaux.hjson.psi.visitors.SimplifyPrintVisitor;
import com.zelaux.hjson.psi.visitors.ToJsonPrintVisitor;

public class SimplifyCodeAction extends MyEditorAction {
    protected SimplifyCodeAction() {
        super(null);
        MyEditorWriteActionHandler<Object> handler = new MyEditorWriteActionHandler<>(this) {
            @Override
            protected void executeWriteAction(Editor editor, DataContext dataContext, Object additionalParameter) {
                Project project = editor.getProject();
                assert project != null;
                Document document = editor.getDocument();
                PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
                AbstractPrintVisitor visitor = new SimplifyPrintVisitor();
                assert file != null;
                file.accept(visitor);
                PsiFile newFile = HJsonFactory.getInstance(project).createDummyFile(visitor.getString());

                CodeStyleManager.getInstance(project).reformatText(
                        newFile,
                        ContainerUtil.newArrayList(newFile.getTextRange())
                );
                document.setText(newFile.getText());
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
