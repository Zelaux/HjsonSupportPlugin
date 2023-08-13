package com.zelaux.hjson.formatter;

import com.intellij.application.options.CodeStyle;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.containers.ContainerUtil;
import com.zelaux.hjson.HJsonFileType;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("JUnitMixedFramework")
public class HJsonInsightTest extends LightJavaCodeInsightFixtureTestCase {
    /**
     * @return path to test data file directory relative to working directory in the run configuration for this test.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData/insight";
    }
/*
    public void testCompletion() {
        myFixture.configureByFiles("CompleteTestData.java", "DefaultTestData.hjson");
        myFixture.complete(CompletionType.BASIC);
        List<String> lookupElementStrings = myFixture.getLookupElementStrings();
        assertNotNull(lookupElementStrings);
        assertSameElements(lookupElementStrings, "key with spaces", "language", "message", "tab", "website");
    }*/
/*

    public void testAnnotator() {
        myFixture.configureByFiles("AnnotatorTestData.java", "DefaultTestData.simple");
        myFixture.checkHighlighting(false, false, true, true);
    }
*/
@Test
    public void testFormatter() {
        myFixture.configureByFile("formatter/FormatterTestData.hjson");
        CodeStyle.getLanguageSettings(myFixture.getFile()).SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
        CodeStyle.getLanguageSettings(myFixture.getFile()).KEEP_BLANK_LINES_IN_CODE = 2;
        WriteCommandAction.writeCommandAction(getProject()).run(() ->
                CodeStyleManager.getInstance(getProject()).reformatText(
                        myFixture.getFile(),
                        ContainerUtil.newArrayList(myFixture.getFile().getTextRange())
                )
        );
    try {
        FileUtil.writeToFile(new File(getTestDataPath()+"/formatter/demo.hjson"),myFixture.getFile().getText());
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    myFixture.checkResultByFile("formatter/DefaultTestData.hjson");
    }

   /* public void testRename() {
        myFixture.configureByFiles("RenameTestData.java", "RenameTestData.simple");
        myFixture.renameElementAtCaret("websiteUrl");
        myFixture.checkResultByFile("RenameTestData.simple", "RenameTestDataAfter.simple", false);
    }*/

   /* public void testFolding() {
        myFixture.configureByFile("DefaultTestData.simple");
        myFixture.testFolding(getTestDataPath() + "/FoldingTestData.java");
    }*/

    /*public void testFindUsages() {
        Collection<UsageInfo> usageInfos = myFixture.testFindUsages("FindUsagesTestData.simple", "FindUsagesTestData.java");
        assertEquals(1, usageInfos.size());
    }*/

    public void testCommenter() {
        myFixture.configureByText(HJsonFileType.INSTANCE, "<caret>member: value");
        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("//member: value");
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("member: value");
    }

    /*public void testReference() {
        PsiReference referenceAtCaret =
                myFixture.getReferenceAtCaretPositionWithAssertion("ReferenceTestData.java", "DefaultTestData.simple");
        final SimpleProperty resolvedSimpleProperty = assertInstanceOf(referenceAtCaret.resolve(), SimpleProperty.class);
        assertEquals("https://en.wikipedia.org/", resolvedSimpleProperty.getValue());
    }*/
/*
    public void testDocumentation() {
        myFixture.configureByFiles("DocumentationTestData.java", "DocumentationTestData.simple");
        final PsiElement originalElement = myFixture.getElementAtCaret();
        PsiElement element = DocumentationManager
                .getInstance(getProject())
                .findTargetElement(myFixture.getEditor(), originalElement.getContainingFile(), originalElement);

        if (element == null) {
            element = originalElement;
        }

        final DocumentationProvider documentationProvider = DocumentationManager.getProviderFromElement(element);
        final String generateDoc = documentationProvider.generateDoc(element, originalElement);
        assertNotNull(generateDoc);
        assertSameLinesWithFile(getTestDataPath() + "/" + "DocumentationTest.html.expected", generateDoc);
    }*/
}