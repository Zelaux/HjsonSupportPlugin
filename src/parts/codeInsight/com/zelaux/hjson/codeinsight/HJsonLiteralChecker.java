package com.zelaux.hjson.codeinsight;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.zelaux.hjson.HJsonBundle;
import com.zelaux.hjson.psi.HJsonLiteral;
import com.zelaux.hjson.psi.HJsonStringLiteral;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class HJsonLiteralChecker {
    public static final Pattern VALID_ESCAPE = Pattern.compile("\\\\([\"\\\\/bfnrt]|u[0-9a-fA-F]{4})");
    private static final Pattern VALID_NUMBER_LITERAL = Pattern.compile("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?");
    public static final String INF = "Infinity";
    public static final String MINUS_INF = "-Infinity";
    public static final String NAN = "NaN";

    @Nullable
    public String getErrorForNumericLiteral(String literalText) {
        if (!INF.equals(literalText) &&
                !MINUS_INF.equals(literalText) &&
                !NAN.equals(literalText) &&
                !VALID_NUMBER_LITERAL.matcher(literalText).matches()) {
            return HJsonBundle.message("syntax.error.illegal.floating.point.literal");
        }
        return null;
    }

    @Nullable
    public Pair<TextRange, String> getErrorForStringFragment(Pair<TextRange, String> fragment, HJsonStringLiteral stringLiteral) {
        if (fragment.getSecond().chars().anyMatch(c -> c <= '\u001F')) { // fragments are cached, string values - aren't; go inside only if we encountered a potentially 'wrong' char
            final String text = stringLiteral.getText();
            if (new TextRange(0, text.length()).contains(fragment.first)) {
                final int startOffset = fragment.first.getStartOffset();
                final String part = text.substring(startOffset, fragment.first.getEndOffset());
                char[] array = part.toCharArray();
                for (int i = 0; i < array.length; i++) {
                    char c = array[i];
                    if (c <= '\u001F') {
                        return Pair.create(new TextRange(startOffset + i, startOffset + i + 1),
                                HJsonBundle
                                        .message("syntax.error.control.char.in.string", "\\u" + Integer.toHexString(c | 0x10000).substring(1)));
                    }
                }
            }
        }
        final String error = getStringError(fragment.second);
        return error == null ? null : Pair.create(fragment.first, error);
    }

    @Nullable
    public static String getStringError(String fragmentText) {
        if (fragmentText.startsWith("\\") && fragmentText.length() > 1 && !VALID_ESCAPE.matcher(fragmentText).matches()) {
            if (fragmentText.startsWith("\\u")) {
                return HJsonBundle.message("syntax.error.illegal.unicode.escape.sequence");
            }
            else {
                return HJsonBundle.message("syntax.error.illegal.escape.sequence");
            }
        }
        return null;
    }

}
