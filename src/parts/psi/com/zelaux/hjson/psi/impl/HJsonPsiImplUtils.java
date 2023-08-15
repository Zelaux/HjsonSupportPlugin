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
import com.intellij.openapi.util.text.StringUtil;
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
    static final Pattern nonSpacePattern = Pattern.compile("[^ ]");
    private static final String ourEscapesTable = "\"\"\\\\//b\bf\fn\nr\rt\t";

    @NotNull
    public static String getName(@NotNull HJsonMember property) {
        String text = property.getMemberName().getText();
        return StringUtil.unescapeStringCharacters(HJsonPsiUtil.stripQuotes(text));
    }

/*    @NotNull
    public static String getName(@NotNull HJsonMemberName memberName) {
        HJsonJsonString jsonString = memberName.getJsonString();
        String text;
        if (jsonString == null) {
            text = memberName.getMemberNameToken().getText();
        } else {
            text = jsonString.getText();
        }
        return StringUtil.unescapeStringCharacters(HJsonPsiUtil.stripQuotes(text));
    }*/

    /*
     */

    /**
     * Actually only JSON string literal should be accepted as valid name of property according to standard,
     * but for compatibility with JavaScript integration any JSON literals as well as identifiers (unquoted words)
     * are possible and highlighted as error later.
     *
     * @see JsonStandardComplianceInspection
     *//*
    @NotNull
    public static HJsonStringLiteral getNameElement(@NotNull HJsonMemberName property) {
        final PsiElement firstChild = property.getFirstChild();
        assert firstChild instanceof HJsonStringLiteral;
        return (HJsonStringLiteral) firstChild;
    }*/
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
            PsiFile file = literal.getContainingFile();

            if (file != null) {
                return getMultilineString(file.getText(), literal.getTextOffset(), literal.getText());
            }

        }
        return StringUtil.unescapeStringCharacters(HJsonPsiUtil.stripQuotes(literal.getText()));

    }

    @NotNull
    public static String getMultilineString(String fileText, int offset, String myText) {
        int lineOffset = offset - StringUtil.lastIndexOf(fileText, '\n', 0, offset) - 1;
        String[] lines = myText.split("\n");
        int newLen = lines.length;
        int startOffset = 1;
        if (lines.length > 0 && lines[lines.length - 1].matches("\\s*'''")) {
            newLen -= 1;
        }
        if (lines.length > 1 && lines[0].equals("'''")) {
            newLen -= 1;
//            startOffset = 0;
        }
        String[] newLines = new String[newLen];

        if (startOffset == 1) {
            newLines[0] = lines[0].substring(3);
        }
        for (int i = 0; i < newLines.length; i++) {
            String line = lines[i + startOffset];
            Matcher matcher = nonSpacePattern.matcher(line);
            int foundIndex = line.length() - 1;
            if (matcher.find()) {
                foundIndex = matcher.start();
            } else if(line.length()==0){
                foundIndex=0;
            }
            int endIndex = line.length();
            int startIndex = Math.min(lineOffset, foundIndex);
            boolean isLast = i + 1 == lines.length;
            if (isLast && line.endsWith("'''")) {
                endIndex -= 3;
                if (startIndex == endIndex) continue;
            }
            newLines[i] = line.substring(startIndex, endIndex);

        }
        return String.join("\n", newLines);
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
