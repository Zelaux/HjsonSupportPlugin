package com.zelaux.hjson.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.zelaux.hjson.HJsonFileType;
import org.jetbrains.annotations.NotNull;

public class HJsonFactory {
    private final Project myProject;

    private HJsonFactory(@NotNull Project project) {
        myProject = project;
    }

    public static HJsonFactory getInstance(Project project) {
        return new HJsonFactory(project);
    }

    /**
     * Create lightweight in-memory {@link HJsonFile} filled with {@code content}.
     *
     * @param content content of the file to be created
     * @return created file
     */
    @NotNull
    public PsiFile createDummyFile(@NotNull String content) {
        final PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(myProject);
        return psiFileFactory.createFileFromText("dummy." + HJsonFileType.INSTANCE.getDefaultExtension(), HJsonFileType.INSTANCE, content);
    }

    /**
     * Create JSON value from supplied content.
     *
     * @param content properly escaped text of JSON value, e.g. Java literal {@code "\"new\\nline\""} if you want to create string literal
     * @param <T>     type of the JSON value desired
     * @return element created from given text
     * @see #createJsonStringLiteral(String)
     */
    @NotNull
    public <T extends HJsonValue> T createValue(@NotNull String content) {
        final PsiFile file = createDummyFile("{f: " + content + "}");
        //noinspection unchecked,ConstantConditions
        return (T) file.getFirstChild().getFirstChild().getNextSibling().getLastChild().getLastChild();
    }

    @NotNull
    public HJsonObject createObject(@NotNull String content) {
        final PsiFile file = createDummyFile("{" + content + "}");
        return (HJsonObject) file.getFirstChild();
    }

    /**
     * Create JSON string literal from supplied <em>unescaped</em> content.
     *
     * @param unescapedContent unescaped content of string literal, e.g. Java literal {@code "new\nline"} (compare with {@link #createValue(String)}).
     * @return JSON string literal created from given text
     */
    @NotNull
    public HJsonJsonString createJsonStringLiteral(@NotNull String unescapedContent) {
        return createJsonStringLiteral('"', unescapedContent);
    }

    public HJsonJsonString createJsonStringLiteral(char quote, @NotNull String unescapedContent) {
        return createValue(quote + StringUtil.escapeStringCharacters(unescapedContent) + quote);
    }

    @NotNull
    public HJsonMember createProperty(@NotNull final String name, @NotNull final String value) {
        final PsiFile file = createDummyFile("{\"" + name + "\": " + value + "}");
        return ((HJsonObject) file.getFirstChild()).getMemberList().get(0);
    }

    @NotNull
    public PsiElement createComma() {
        final @NotNull PsiFile file = createDummyFile("[1,]");
        return file.getFirstChild()//<<array>>
                .getLastChild()//']'
                .getPrevSibling()//','
                ;
    }

    public PsiElement createMemberName(String s) {
        PsiFile file;
        if (s.contains(" ")) {
            file = createDummyFile("\"" + s + "\":nil");
        } else {
            file = createDummyFile(s + ":nil");
        }
        return file.getFirstChild()//<<object>>
                .getFirstChild()//<<member>
                .getFirstChild()//memberNameToken
                ;
    }
}
