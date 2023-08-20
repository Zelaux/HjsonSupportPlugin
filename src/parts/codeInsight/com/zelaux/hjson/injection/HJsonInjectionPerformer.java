package com.zelaux.hjson.injection;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.injection.general.Injection;
import com.intellij.lang.injection.general.LanguageInjectionPerformer;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.FallbackInjectionPerformer;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.injection.ReferenceInjector;
import com.intellij.util.SmartList;
import com.zelaux.hjson.psi.HJsonElement;
import com.zelaux.hjson.psi.HJsonMultilineString;
import com.zelaux.hjson.psi.impl.HJsonPsiImplUtils;
import org.intellij.plugins.intelliLang.inject.InjectedLanguage;
import org.intellij.plugins.intelliLang.inject.InjectorUtils;
import org.intellij.plugins.intelliLang.inject.LanguageInjectionSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HJsonInjectionPerformer implements LanguageInjectionPerformer {

    @Override
    public boolean isPrimary() {
        return true;
    }
static boolean useDefault=false;
    @Override
    public boolean performInjection(@NotNull MultiHostRegistrar registrar, @NotNull Injection injection, @NotNull PsiElement context) {
//        var injectorAdapter = JavaConcatenationToInjectorAdapter(context.project)
//        var (_, operands) = injectorAdapter.computeAnchorAndOperands(context)
        if (!(context instanceof HJsonMultilineString) || useDefault) {
            if (context instanceof HJsonElement) {
                FallbackInjectionPerformer performer = FallbackInjectionPerformer.getInstance();
                if (performer != null) return performer.performInjection(registrar, injection, context);
            }
            return false;
        }
        var operand = (HJsonMultilineString) context;
        var containingFile = context.getContainingFile();


        var language = injection.getInjectedLanguage();
        if (language == null) return false;

        var trinities = new SmartList<Trinity<PsiLanguageInjectionHost, InjectedLanguage, TextRange>>();
        var pendingPrefix = injection.getPrefix();

        /*for (operand in operands.slice(0 until operands.size - 1)) {
            if (operand !is PsiLanguageInjectionHost) continue
                    var injectionPart = InjectedLanguage.create(injection.injectedLanguageId,
                    pendingPrefix,
                    null, false) ?: continue
                    pendingPrefix = ""
            trinities.add(Trinity.create(operand, injectionPart, ElementManipulators.getValueTextRange(operand)))
        }*/

        InjectedLanguage injectionPart = InjectedLanguage.create(injection.getInjectedLanguageId(),
                pendingPrefix,
                injection.getSuffix(), false);
        if (operand instanceof PsiLanguageInjectionHost) {

            var operand_ = (PsiLanguageInjectionHost) operand;
//            trinities.add(Trinity.create(operand_, injectionPart, ElementManipulators.getValueTextRange(operand_)));
        }

        ArrayList<TextRange> textRanges = HJsonPsiImplUtils.getMultilineContentRanges(containingFile.getText(), context.getTextOffset(), context.getText(), true);
        for (TextRange range : textRanges) {
            trinities.add(Trinity.create((PsiLanguageInjectionHost) operand, injectionPart, range));
        }
        InjectorUtils.registerInjection(language, trinities, containingFile, registrar);
        String supportId = injection.getSupportId();
        if (supportId != null) {
            LanguageInjectionSupport support = InjectorUtils.findInjectionSupport(supportId);
            if (support != null) {
                InjectorUtils.registerSupport(support, false, context, language);
            }
        }
        return true;
    }

    public static void registerInjection(@Nullable Language language,
                                         @NotNull List<? extends Trinity<PsiLanguageInjectionHost, InjectedLanguage, TextRange>> list,
                                         @NotNull PsiFile containingFile,
                                         @NotNull MultiHostRegistrar registrar) {
        // if language isn't injected when length == 0, subsequent edits will not cause the language to be injected as well.
        // Maybe IDEA core is caching a bit too aggressively here?
        if (language == null/* && (pair.second.getLength() > 0*/) {
            return;
        }
        ParserDefinition parser = LanguageParserDefinitions.INSTANCE.forLanguage(language);
        ReferenceInjector injector = ReferenceInjector.findById(language.getID());
        if (parser == null && injector != null) {
            for (Trinity<PsiLanguageInjectionHost, InjectedLanguage, TextRange> trinity : list) {
                String prefix = trinity.second.getPrefix();
                String suffix = trinity.second.getSuffix();
                PsiLanguageInjectionHost host = trinity.first;
                TextRange textRange = trinity.third;
                InjectedLanguageUtil.injectReference(registrar, language, prefix, suffix, host, textRange);
                return;
            }
            return;
        }
        boolean injectionStarted = false;
        for (Trinity<PsiLanguageInjectionHost, InjectedLanguage, TextRange> t : list) {
            PsiLanguageInjectionHost host = t.first;
            if (host.getContainingFile() != containingFile || !host.isValidHost()) continue;

            TextRange textRange = t.third;
            InjectedLanguage injectedLanguage = t.second;

            if (!injectionStarted) {
                // TextMate language requires file extension
                if (!StringUtil.equalsIgnoreCase(language.getID(), t.second.getID())) {
                    registrar.startInjecting(language, StringUtil.toLowerCase(t.second.getID()));
                } else {
                    registrar.startInjecting(language);
                }
                injectionStarted = true;
            }
            registrar.addPlace(injectedLanguage.getPrefix(), injectedLanguage.getSuffix(), host, textRange);
        }
        if (injectionStarted) {
            registrar.doneInjecting();
        }
    }
}
