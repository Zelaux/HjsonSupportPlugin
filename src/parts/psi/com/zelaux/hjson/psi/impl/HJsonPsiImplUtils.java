package com.zelaux.hjson.psi.impl;

import com.intellij.icons.AllIcons;
import com.intellij.json.JsonBundle;
import com.intellij.json.JsonDialectUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.PlatformIcons;
import com.zelaux.hjson.HJsonLanguage;
import com.zelaux.hjson.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HJsonPsiImplUtils {
    static final Key<List<Pair<TextRange, String>>> STRING_FRAGMENTS = new Key<>("JSON string fragments");
    public static final Pattern nonSpacePattern = Pattern.compile("[^ ]");
    private static final String ourEscapesTable = "\"\"\\\\//b\bf\fn\nr\rt\t";

    @NotNull
    public static String getName(@NotNull HJsonMember property) {
        String text = property.getMemberName().getText();
        return StringUtil.unescapeStringCharacters(HJsonPsiUtil.stripQuotes(text));
    }


    @Nullable
    public static HJsonValue getValue(@NotNull HJsonMember property) {
        HJsonMemberValue memberValue = property.getMemberValue();
        return memberValue == null ? null : memberValue.getValue();
    }
/*
    public static boolean isQuotedString(@NotNull HJsonLiteral literal) {
        return literal.getNode().findChildByType(HJsonParserDefinition.STRING_LITERALS) != null;
    }*/

    @Nullable
    public static ItemPresentation getPresentation(@NotNull final HJsonMember property) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return property.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                final HJsonValue value = property.getValue();
                return value instanceof HJsonLiteral ? value.getText() : null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                if (property.getValue() instanceof HJsonArray) {
                    return AllIcons.Json.Array;
                }
                if (property.getValue() instanceof HJsonObject) {
                    return AllIcons.Json.Object;
                }
                return PlatformIcons.PROPERTY_ICON;
            }
        };
    }

    @Nullable
    public static ItemPresentation getPresentation(@NotNull final HJsonArray array) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return JsonBundle.message("json.array");
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return AllIcons.Json.Array;
            }
        };
    }

    @Nullable
    public static ItemPresentation getPresentation(@NotNull final HJsonObject object) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return JsonBundle.message("json.object");
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return AllIcons.Json.Object;
            }
        };
    }

    @NotNull
    public static List<Pair<TextRange, String>> getTextFragments(@NotNull HJsonStringLiteral literal) {
        List<Pair<TextRange, String>> result = literal.getUserData(STRING_FRAGMENTS);
        if (result == null) {
            result = new ArrayList<>();
            final String text = literal.getText();
            final int length = text.length();
            int pos = 1, unescapedSequenceStart = 1;
            while (pos < length) {
                if (text.charAt(pos) == '\\') {
                    if (unescapedSequenceStart != pos) {
                        result.add(Pair.create(new TextRange(unescapedSequenceStart, pos), text.substring(unescapedSequenceStart, pos)));
                    }
                    if (pos == length - 1) {
                        result.add(Pair.create(new TextRange(pos, pos + 1), "\\"));
                        break;
                    }
                    final char next = text.charAt(pos + 1);
                    switch (next) {
                        case '"':
                        case '\\':
                        case '/':
                        case 'b':
                        case 'f':
                        case 'n':
                        case 'r':
                        case 't':
                            final int idx = ourEscapesTable.indexOf(next);
                            result.add(Pair.create(new TextRange(pos, pos + 2), ourEscapesTable.substring(idx + 1, idx + 2)));
                            pos += 2;
                            break;
                        case 'u':
                            int i = pos + 2;
                            for (; i < pos + 6; i++) {
                                if (i == length || !StringUtil.isHexDigit(text.charAt(i))) {
                                    break;
                                }
                            }
                            result.add(Pair.create(new TextRange(pos, i), text.substring(pos, i)));
                            pos = i;
                            break;
                        case 'x':
                            Language language = JsonDialectUtil.getLanguageOrDefaultJson(literal);
                            if (language instanceof HJsonLanguage && ((HJsonLanguage) language).hasPermissiveStrings()) {
                                int i2 = pos + 2;
                                for (; i2 < pos + 4; i2++) {
                                    if (i2 == length || !StringUtil.isHexDigit(text.charAt(i2))) {
                                        break;
                                    }
                                }
                                result.add(Pair.create(new TextRange(pos, i2), text.substring(pos, i2)));
                                pos = i2;
                                break;
                            }
                        default:
                            result.add(Pair.create(new TextRange(pos, pos + 2), text.substring(pos, pos + 2)));
                            pos += 2;
                    }
                    unescapedSequenceStart = pos;
                } else {
                    pos++;
                }
            }
            final int contentEnd = text.charAt(0) == text.charAt(length - 1) ? length - 1 : length;
            if (unescapedSequenceStart < contentEnd) {
                result.add(Pair.create(new TextRange(unescapedSequenceStart, contentEnd), text.substring(unescapedSequenceStart, contentEnd)));
            }
            result = Collections.unmodifiableList(result);
            literal.putUserData(STRING_FRAGMENTS, result);
        }
        return result;
    }

    public static void delete(@NotNull HJsonMember property) {
        final ASTNode myNode = property.getNode();
        HJsonPsiChangeUtils.removeCommaSeparatedFromList(myNode, myNode.getTreeParent());
    }

    @NotNull
    public static String getValue(@NotNull HJsonStringLiteral literal) {
        if (literal instanceof HJsonMultilineString) {
            return String.join("\n",getLines((HJsonMultilineString) literal));

        }
        return StringUtil.unescapeStringCharacters(HJsonPsiUtil.stripQuotes(literal.getText()));

    }

    public static String[] getLines(@NotNull PsiElement string) {
        PsiElement file = findRootNode(string);
        return getMultilineStringLines(file.getText(), string.getTextOffset(), string.getText());
    }

    @NotNull
    private static PsiElement findRootNode(@NotNull PsiElement string) {
        PsiElement file = string.getContainingFile();

        if (file == null) {
            file= string;
            while (file.getParent()!=null && !(file instanceof PsiFile)){
                file=file.getParent();
            }
        }
        return file;
    }

    @NotNull
    public static ArrayList<TextRange> getMultilineContentRanges(String fileText, int offset, String myText, boolean includeLineSeparators) {
        int lineIndent = offset - StringUtil.lastIndexOf(fileText, '\n', 0, offset) - 1;
        LineTokenizer lineTokenizer = new LineTokenizer(myText);
        ArrayList<TextRange> ranges = new ArrayList<>();

        while (!lineTokenizer.atEnd()) {
            int lineStart = lineTokenizer.getOffset();
            int lineLength = lineTokenizer.getLength();
            int lineEnd = lineStart + lineLength;
            int lineSeparatorLength = lineTokenizer.getLineSeparatorLength();

            handle:
            {


                int startIndex;

                int endIndex = lineEnd;
                if (lineStart == 0) {
                    if (lineLength <= 3) break handle;
                    startIndex = 3;
                } else{
                    Matcher matcher = nonSpacePattern.matcher(myText);
                    int foundIndex = lineEnd - 1;
                    if (matcher.find(lineStart) && matcher.end() <= lineEnd) {
                        foundIndex = matcher.start();
                    } else if (lineLength == 0) {
                        foundIndex = lineStart;
                    }
                    startIndex = Math.min(lineIndent + lineStart, foundIndex);
                }
                boolean isLast = lineEnd == myText.length();
                if (isLast && myText.endsWith("'''")) {
                    endIndex -= 3;
                    if (startIndex == endIndex && ranges.size() > 0) break handle;
                } else {
                    if (includeLineSeparators) {
                        endIndex += lineSeparatorLength;
                    }
                }

                ranges.add(new TextRange(startIndex, endIndex));
            }
            lineTokenizer.advance();
        }
        return ranges;
    }

    public static ArrayList<TextRange> getMultiLineRanges(PsiElement psi,boolean includeLineSeparators) {
        return getMultilineContentRanges(findRootNode(psi).getText(),psi.getTextOffset(),psi.getText(),includeLineSeparators);
    }
    @NotNull
    public static String getMultilineString(String fileText, int offset, String myText) {
        String[] newLines = getMultilineStringLines(fileText, offset, myText);
        return String.join("\n", newLines);
    }
    public static int getIndent(HJsonMultilineString multilineString){
        return getIndent((PsiElement)multilineString);
    }
    public static int getIndent(PsiElement multilineString){
        int offset = multilineString.getTextOffset();
        PsiElement node = findRootNode(multilineString);
        return offset - StringUtil.lastIndexOf(node.getText(), '\n', 0, offset) - 1;
    }

    @NotNull
    public static String[] getMultilineStringLines(String fileText, int offset, String myText) {
        ArrayList<TextRange> ranges = getMultilineContentRanges(fileText, offset, myText, false);
        String[] newLines = new String[ranges.size()];
        for (int i = 0; i < ranges.size(); i++) {
            newLines[i] = ranges.get(i).substring(myText);
        }
        return newLines;
    }

    /*public static boolean isPropertyName(@NotNull HJsonStringLiteral literal) {
        final PsiElement parent = literal.getParent();
        return parent instanceof HJsonMember && ((HJsonMember) parent).getMemberName() == literal;
    }*/

    public static boolean getValue(@NotNull HJsonBooleanLiteral literal) {
        return literal.textMatches("true");
    }

    public static double getValue(@NotNull HJsonNumberLiteral literal) {
        return Double.parseDouble(literal.getText());
    }

}
