package com.zelaux.hjson;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class HJsonNamesValidator implements NamesValidator {

    private final HJsonLexer myLexer = new HJsonLexer();

    @Override
    public synchronized boolean isKeyword(@NotNull String name, Project project) {
        myLexer.start(name);
        return HJsonParserDefinition.KEYWORDS.contains(myLexer.getTokenType()) && myLexer.getTokenEnd() == name.length();
    }

    @Override
    public synchronized boolean isIdentifier(@NotNull String name, final Project project) {
        /*if (!StringUtil.startsWithChar(name,'\'') && !StringUtil.startsWithChar(name,'"')) {
            name =  name;
        }

        if (!StringUtil.endsWithChar(name,'"') && !StringUtil.endsWithChar(name,'\'')) {
            name += "\"";
        }*/

        myLexer.start(name);
        IElementType type = myLexer.getTokenType();

        return myLexer.getTokenEnd() == name.length() && (type == HJsonElementTypes.DOUBLE_QUOTED_STRING ||
                type == HJsonElementTypes.SINGLE_QUOTED_STRING || type == HJsonElementTypes.QUOTELESS_STRING);
    }

}
