package com.zelaux.hjson.ast;

import com.intellij.lang.DefaultASTFactoryImpl;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.zelaux.hjson.HJsonElementTypes;
import com.zelaux.hjson.psi.HJsonTokens;
import org.jetbrains.annotations.NotNull;

public class HJsonASTFactory extends DefaultASTFactoryImpl {
    private static TokenSet goUp = TokenSet.create(
            HJsonElementTypes.QUOTELESS_STRING,
            HJsonElementTypes.MEMBER_VALUE,
            HJsonElementTypes.MEMBER
    );

    @Override
    public CompositeElement createComposite(@NotNull IElementType type) {
        /*if (type == HJsonElementTypes.MEMBER) {
            return new MemberCompositeElement(type);
        }*/
        /*if (goUp.contains(type)) {
            return new GoUpNewLineComposite(type);
        }*/
        if (type == GeneratedParserUtilBase.DUMMY_BLOCK) {
            return new CompositeElement(type) {


                @Override
                protected PsiElement createPsiNoLock() {
                    return new CompositePsiElement(type) {
                    };
                }
            };
        }
        return super.createComposite(type);

    }

    public static class MemberCompositeElement extends CompositeElement {
        public MemberCompositeElement(@NotNull IElementType type) {
            super(type);
        }

        @Override
        public void rawAddChildrenWithoutNotifications(@NotNull TreeElement first) {
            super.rawAddChildrenWithoutNotifications(first);
        }
    }

    private static class GoUpNewLineComposite extends CompositeElement {
        boolean foundNewLine;

        public GoUpNewLineComposite(@NotNull IElementType type) {
            super(type);
        }

        @Override
        protected PsiElement createPsiNoLock() {

            return super.createPsiNoLock();
        }

        @Override
        public void rawAddChildrenWithoutNotifications(@NotNull TreeElement first) {
            if (foundNewLine || HJsonTokens.WHITE_SPACES.contains(first.getElementType())) {
                if (foundNewLine || first.getText().contains("\n")) {
                    foundNewLine = true;
                    getTreeParent().rawAddChildrenWithoutNotifications(first);
                    return;
                }
            }
            super.rawAddChildrenWithoutNotifications(first);
        }
    }
}
