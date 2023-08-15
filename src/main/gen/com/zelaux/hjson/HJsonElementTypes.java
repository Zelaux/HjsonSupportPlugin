// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.zelaux.hjson.psi.impl.*;

public interface HJsonElementTypes {

  HJsonElementType ARRAY = new HJsonElementType("ARRAY");
  HJsonElementType BOOLEAN_LITERAL = new HJsonElementType("BOOLEAN_LITERAL");
  HJsonElementType JSON_STRING = new HJsonElementType("JSON_STRING");
  HJsonElementType LITERAL = new HJsonElementType("LITERAL");
  HJsonElementType MEMBER = new HJsonElementType("MEMBER");
  HJsonElementType MEMBER_VALUE = new HJsonElementType("MEMBER_VALUE");
  HJsonElementType MULTILINE_STRING = new HJsonElementType("MULTILINE_STRING");
  HJsonElementType NULL_LITERAL = new HJsonElementType("NULL_LITERAL");
  HJsonElementType NUMBER_LITERAL = new HJsonElementType("NUMBER_LITERAL");
  HJsonElementType OBJECT = new HJsonElementType("OBJECT");
  HJsonElementType OBJECT_FULL = new HJsonElementType("OBJECT_FULL");
  HJsonElementType QUOTELESS_STRING = new HJsonElementType("QUOTELESS_STRING");
  HJsonElementType STRING_LITERAL = new HJsonElementType("STRING_LITERAL");
  HJsonElementType VALUE = new HJsonElementType("VALUE");

  HJsonTokenType BLOCK_COMMENT_TOKEN = new HJsonTokenType("BLOCK_COMMENT_TOKEN");
  HJsonTokenType COLON = new HJsonTokenType(":");
  HJsonTokenType COMMA = new HJsonTokenType(",");
  HJsonTokenType DOUBLE_QUOTE = new HJsonTokenType("\"");
  HJsonTokenType DOUBLE_QUOTED_STRING_TOKEN = new HJsonTokenType("DOUBLE_QUOTED_STRING_TOKEN");
  HJsonTokenType FALSE = new HJsonTokenType("false");
  HJsonTokenType LINE_COMMENT_TOKEN = new HJsonTokenType("LINE_COMMENT_TOKEN");
  HJsonTokenType L_BRACKET = new HJsonTokenType("[");
  HJsonTokenType L_CURLY = new HJsonTokenType("{");
  HJsonTokenType MEMBER_NAME = new HJsonTokenType("MEMBER_NAME");
  HJsonTokenType MULTILINE_STRING_TOKEN = new HJsonTokenType("MULTILINE_STRING_TOKEN");
  HJsonTokenType NULL = new HJsonTokenType("null");
  HJsonTokenType NUMBER_TOKEN = new HJsonTokenType("NUMBER_TOKEN");
  HJsonTokenType QUOTELESS_STRING_TOKEN = new HJsonTokenType("QUOTELESS_STRING_TOKEN");
  HJsonTokenType R_BRACKET = new HJsonTokenType("]");
  HJsonTokenType R_CURLY = new HJsonTokenType("}");
  HJsonTokenType SINGLE_QUOTE = new HJsonTokenType("'");
  HJsonTokenType SINGLE_QUOTED_STRING_TOKEN = new HJsonTokenType("SINGLE_QUOTED_STRING_TOKEN");
  HJsonTokenType TRUE = new HJsonTokenType("true");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARRAY) {
        return new HJsonArrayImpl(node);
      }
      else if (type == BOOLEAN_LITERAL) {
        return new HJsonBooleanLiteralImpl(node);
      }
      else if (type == JSON_STRING) {
        return new HJsonJsonStringImpl(node);
      }
      else if (type == MEMBER) {
        return new HJsonMemberImpl(node);
      }
      else if (type == MEMBER_VALUE) {
        return new HJsonMemberValueImpl(node);
      }
      else if (type == MULTILINE_STRING) {
        return new HJsonMultilineStringImpl(node);
      }
      else if (type == NULL_LITERAL) {
        return new HJsonNullLiteralImpl(node);
      }
      else if (type == NUMBER_LITERAL) {
        return new HJsonNumberLiteralImpl(node);
      }
      else if (type == OBJECT) {
        return new HJsonObjectImpl(node);
      }
      else if (type == OBJECT_FULL) {
        return new HJsonObjectFullImpl(node);
      }
      else if (type == QUOTELESS_STRING) {
        return new HJsonQuotelessStringImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
