package com.zelaux.hjson.formatter.multiline;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessorHelper;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.*;
import com.zelaux.hjson.psi.impl.HJsonRecursiveElementVisitor;
import org.jetbrains.annotations.NotNull;

public class MultilineIndentVisitor extends HJsonRecursiveElementVisitor {

    private final PostFormatProcessorHelper myPostProcessor;

    public MultilineIndentVisitor(CodeStyleSettings settings) {
        myPostProcessor = new PostFormatProcessorHelper(settings.getCommonSettings(HJsonLanguage.INSTANCE));
    }

    public PsiElement process(PsiElement formatted) {
        formatted.accept(this);
        return formatted;

    }

    public TextRange processText(final PsiFile source, final TextRange rangeToReformat) {
        myPostProcessor.setResultTextRange(rangeToReformat);
        source.accept(this);
        return myPostProcessor.getResultTextRange();
    }

    protected boolean checkElementOverlapsRange(final PsiElement element) {
        return myPostProcessor.isElementPartlyInRange(element);
    }

    @Override
    public void visitMultilineString(@NotNull HJsonMultilineString o) {
        super.visitMultilineString(o);
        if (!checkElementOverlapsRange(o)) return;
        if (!(o.getParent() instanceof HJsonArray)) {
            return;
        }

        PsiElement root = o;
        CommonCodeStyleSettings.IndentOptions options = myPostProcessor.getSettings().getIndentOptions();
        int indentCount = -options.INDENT_SIZE;
        while (root.getParent() != null && !(root instanceof PsiFile)) {
            root = root.getParent();
            assert options != null;
            if (root instanceof HJsonContainer) indentCount += options.INDENT_SIZE;
        }
        int offset = o.getTextOffset();

        String rootText = root.getText();
        int lineIndex = StringUtil.lastIndexOf(rootText, '\n', 0, offset);
        int lineOffset = offset - lineIndex - 1;
        String indent = " ".repeat(indentCount);
        if (options.USE_TAB_CHARACTER) {
            indent = indent.replace("    ", "\t");
        }
        String[] lines = o.getLines();

        StringBuilder builder = new StringBuilder("'''\n");
        for (String line : lines) {
            builder.append(indent).append(line).append("\n");
        }
        builder.append(indent).append("'''");
        Document document = PsiDocumentManager.getInstance(o.getProject()).getDocument(o.getContainingFile());
        boolean isEmptyStart=true;
        for (int i = 1; i <= lineOffset; i++) {
            if(document.getCharsSequence().charAt(lineIndex+i)!=' ') {
                isEmptyStart=false;
                break;
            }
        }
        PsiElement prevSibling = o.getPrevSibling();
        if(prevSibling instanceof PsiWhiteSpace){
            String prevWhiteSpaceText = prevSibling.getNode().getText();
            int i = prevWhiteSpaceText.lastIndexOf('\n');
            if(i!=-1){
//                ((LeafElement)prevSibling.getNode()).replaceWithText("\n"+indent);
            }
        }
        if(isEmptyStart && false){

//            document.replaceString(lineOffset,o.getTextOffset(),builder.toString());
        } else {
//            ((LeafElement)o.getFirstChild().getNode()).replaceWithText(builder.toString());
//            o.replace(HJsonFactory.getInstance(o.getProject()).createValue(builder.toString()));
        }
    }

    @Override
    public void visitMemberValue(@NotNull HJsonMemberValue o) {
        super.visitMemberValue(o);


    }
}

