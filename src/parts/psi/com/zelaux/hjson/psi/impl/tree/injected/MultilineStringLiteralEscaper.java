/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zelaux.hjson.psi.impl.tree.injected;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.zelaux.hjson.psi.HJsonMultilineString;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MultilineStringLiteralEscaper<T extends HJsonMultilineString & PsiLanguageInjectionHost> extends LiteralTextEscaper<T> {
    private int[] outSourceOffsets;

    public MultilineStringLiteralEscaper(T host) {
        super(host);
    }

    ArrayList<TextRange> ranges;

    @Override
    public boolean decode(@NotNull final TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
        String myText = myHost.getText();
        ranges = HJsonPsiImplUtils.getMultilineContentRanges(myHost.getContainingFile().getText(), myHost.getTextOffset(), myText, true);
        int totalChars = 0;
        for (int i = 0; i < ranges.size(); i++) {
            TextRange range = ranges.get(i);
            int realStart = Math.max(rangeInsideHost.getStartOffset(), range.getStartOffset());
            int realEnd = Math.min(rangeInsideHost.getEndOffset(), range.getEndOffset());
            if (realEnd - realStart < 0) {
                ranges.remove(i);
                i--;
                continue;
            }
            if (realStart != range.getStartOffset() || realEnd != range.getEndOffset()) {
                ranges.set(i, new TextRange(realStart, realEnd));
            }
            totalChars += ranges.get(i).getLength();
        }
        outSourceOffsets = new int[totalChars + 1];

        for (int i = 0,localCharIndex=0; i < ranges.size(); i++) {
            TextRange range = ranges.get(i);
            for (int j = 0; j < range.getLength(); j++) {
                int charIndex = range.getStartOffset() + j;
                outChars.append(myText.charAt(charIndex));
                outSourceOffsets[localCharIndex]=charIndex;
                outSourceOffsets[localCharIndex+1]=charIndex+1;
                localCharIndex++;
            }
        }
        return true;
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, @NotNull final TextRange rangeInsideHost) {
        int result = offsetInDecoded < outSourceOffsets.length ? outSourceOffsets[offsetInDecoded] : -1;
        if (result == -1) return -1;
        return Math.min(result, rangeInsideHost.getLength()) + rangeInsideHost.getStartOffset();
    }

    @Override
    public boolean isOneLine() {
        return true;
    }
}
