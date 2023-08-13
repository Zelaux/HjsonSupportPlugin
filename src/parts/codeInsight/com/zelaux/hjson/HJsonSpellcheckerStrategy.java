package com.zelaux.hjson;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.inspections.PlainTextSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import com.zelaux.hjson.psi.HJsonStringLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HJsonSpellcheckerStrategy extends SpellcheckingStrategy {
    private final Tokenizer<HJsonStringLiteral> ourStringLiteralTokenizer = new Tokenizer<>() {
        @Override
        public void tokenize(@NotNull HJsonStringLiteral element, TokenConsumer consumer) {
            final PlainTextSplitter textSplitter = PlainTextSplitter.getInstance();
            if (element.textContains('\\')) {
                final List<Pair<TextRange, String>> fragments = element.getTextFragments();
                for (Pair<TextRange, String> fragment : fragments) {
                    final TextRange fragmentRange = fragment.getFirst();
                    final String escaped = fragment.getSecond();
                    // Fragment without escaping, also not a broken escape sequence or a unicode code point
                    if (escaped.length() == fragmentRange.getLength() && !escaped.startsWith("\\")) {
                        consumer.consumeToken(element, escaped, false, fragmentRange.getStartOffset(), TextRange.allOf(escaped), textSplitter);
                    }
                }
            }
            else {
                consumer.consumeToken(element, textSplitter);
            }
        }
    };

    @NotNull
    @Override
    public Tokenizer<?> getTokenizer(PsiElement element) {
        if (element instanceof HJsonStringLiteral) {
            return /*new HJsonSchemaSpellcheckerClientForJson((HJsonStringLiteral)element).matchesNameFromSchema()
                    ? EMPTY_TOKENIZER
                    : */ourStringLiteralTokenizer;
        }
        return super.getTokenizer(element);
    }
/*
    private static class HJsonSchemaSpellcheckerClientForJson extends HJsonSchemaSpellcheckerClient {
        @NotNull private final HJsonStringLiteral element;

        protected HJsonSchemaSpellcheckerClientForJson(@NotNull HJsonStringLiteral element) {
            this.element = element;
        }

        @Override
        protected @NotNull HJsonStringLiteral getElement() {
            return element;
        }

        @Override
        protected @NotNull String getValue() {
            return element.getValue();
        }
    }*/
}
