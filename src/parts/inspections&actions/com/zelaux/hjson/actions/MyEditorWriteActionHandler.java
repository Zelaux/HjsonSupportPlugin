package com.zelaux.hjson.actions;

import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.CaretSpecificDataContext;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * @see <a href="https://github.com/krasa/StringManipulation/blob/master/src/main/java/osmedile/intellij/stringmanip/MyEditorWriteActionHandler.java">Reference</a>
 */
public abstract class MyEditorWriteActionHandler<T> extends EditorActionHandler {

    protected final MyDelegator delegator = new MyDelegator();
    public boolean canRunNotForAllCaret = false;
    public final AnAction owner;

    public MyEditorWriteActionHandler(AnAction owner) {
        super(false);
        this.owner = owner;
    }

    boolean isWorksInInjected() {
        return owner.isInInjectedContext();
    }

    private boolean inCheck;

    @Override
    public boolean isEnabled(Editor editor, DataContext dataContext) {
        if (inCheck) {
            return true;
        }
        inCheck = true;
        try {
            if (canRunNotForAllCaret) return super.isEnabled(editor, dataContext);
            if (editor == null) {
                return false;
            }
            Editor hostEditor = dataContext == null ? null : CommonDataKeys.HOST_EDITOR.getData(dataContext);
            if (hostEditor == null) {
                hostEditor = editor;
            }
            final boolean[] result = new boolean[1];
            final CaretTask check = (___, __) -> result[0] = true;
            if (runForAllCarets()) {
                hostEditor.getCaretModel().runForEachCaret(caret -> doIfEnabled(caret, dataContext, check));
            } else {
                doIfEnabled(hostEditor.getCaretModel().getCurrentCaret(), dataContext, check);
            }
            return result[0];
        } finally {
            inCheck = false;
        }

    }


    private void doIfEnabled(@NotNull Caret hostCaret, @Nullable DataContext context, @NotNull CaretTask task) {
        DataContext caretContext = context == null ? null : CaretSpecificDataContext.create(context, hostCaret);
        Editor editor = hostCaret.getEditor();
        if (isWorksInInjected() && caretContext != null) {
            DataContext injectedCaretContext = AnActionEvent.getInjectedDataContext(caretContext);
            Caret injectedCaret = CommonDataKeys.CARET.getData(injectedCaretContext);
            if (injectedCaret != null && injectedCaret != hostCaret && isEnabledForCaret(injectedCaret.getEditor(), injectedCaret, injectedCaretContext)) {
                task.perform(injectedCaret, injectedCaretContext);
                return;
            }
        }
        if (isEnabledForCaret(editor, hostCaret, caretContext)) {
            task.perform(hostCaret, caretContext);
        }
    }

    @Override
    protected final void doExecute(final Editor editor, @Nullable final Caret caret, final DataContext dataContext) {


            final @NotNull ExecutionResult<T> additionalParameter = beforeWriteAction(editor, dataContext);
            if (additionalParameter.isEmpty()) {
                return;
            }
            delegator.additionalParameter = additionalParameter;
            delegator.doExecute(editor, caret, dataContext);

    }


    protected abstract void executeWriteAction(Editor editor, DataContext dataContext, T additionalParameter);

    @NotNull
    protected ExecutionResult<T> beforeWriteAction(Editor editor, DataContext dataContext) {
        return continueExecution();
    }

    protected final ExecutionResult<T> stopExecution() {
        return ExecutionResult.empty();
    }


    protected final ExecutionResult<T> continueExecution() {
        return ExecutionResult.nullContent();
    }

    protected class MyDelegator extends EditorWriteActionHandler {
        public ExecutionResult<T> additionalParameter;

        protected MyDelegator() {
            super(false);
        }

        @Override
        public void executeWriteAction(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {

            MyEditorWriteActionHandler.this.executeWriteAction(editor, dataContext, additionalParameter.unwrapOrNull());
            additionalParameter = null;
        }
    }

    @FunctionalInterface
    private interface CaretTask {
        void perform(@NotNull Caret caret, @Nullable DataContext dataContext);
    }
}