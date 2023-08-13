package com.zelaux.hjson.psi;

import com.zelaux.hjson.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;

public class HJsonPsiChangeUtils {
    public static void removeCommaSeparatedFromList(final ASTNode myNode, final ASTNode parent) {
        ASTNode from = myNode, to = myNode.getTreeNext();

        boolean seenComma = false;

        ASTNode toCandidate = to;
        while (toCandidate != null && toCandidate.getElementType() == TokenType.WHITE_SPACE) {
            toCandidate = toCandidate.getTreeNext();
        }

        if (toCandidate != null && toCandidate.getElementType() == HJsonElementTypes.COMMA) {
            toCandidate = toCandidate.getTreeNext();
            to = toCandidate;
            seenComma = true;

            if (to != null && to.getElementType() == TokenType.WHITE_SPACE) {
                to = to.getTreeNext();
            }
        }

        if (!seenComma) {
            ASTNode treePrev = from.getTreePrev();

            while (treePrev != null && treePrev.getElementType() == TokenType.WHITE_SPACE) {
                from = treePrev;
                treePrev = treePrev.getTreePrev();
            }
            if (treePrev != null && treePrev.getElementType() == HJsonElementTypes.COMMA) {
                from = treePrev;
            }
        }

        parent.removeRange(from, to);
    }
}
