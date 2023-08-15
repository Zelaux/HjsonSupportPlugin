// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.zelaux.hjson.psi.impl.*;

public interface HJsonElementTypes {

  IElementType ARRAY = new HJsonElementType("ARRAY");
  IElementType BOOLEAN_LITERAL = new HJsonElementType("BOOLEAN_LITERAL");
  IElementType JSON_STRING = new HJsonElementType("JSON_STRING");
  IElementType LITERAL = new HJsonElementType("LITERAL");
  IElementType MEMBER = new HJsonElementType("MEMBER");
  IElementType MEMBER_NAME = new HJsonElementType("MEMBER_NAME");
  IElementType MEMBER_VALUE = new HJsonElementType("MEMBER_VALUE");
  IElementType MULTILINE_STRING = new HJsonElementType("MULTILINE_STRING");
  IElementType NULL_LITERAL = new HJsonElementType("NULL_LITERAL");
  IElementType NUMBER_LITERAL = new HJsonElementType("NUMBER_LITERAL");
  IElementType OBJECT = new HJsonElementType("OBJECT");
  IElementType OBJECT_FULL = new HJsonElementType("OBJECT_FULL");
  IElementType QUOTE_LESS_PART_STRING = new HJsonElementType("QUOTE_LESS_PART_STRING");
  IElementType QUOTE_LESS_STRING = new HJsonElementType("QUOTE_LESS_STRING");
  IElementType STRING_LITERAL = new HJsonElementType("STRING_LITERAL");
  IElementType VALUE = new HJsonElementType("VALUE");

  IElementType BLOCK_COMMENT = new HJsonTokenType("BLOCK_COMMENT");
  IElementType COLON = new HJsonTokenType(":");
  IElementType COMMA = new HJsonTokenType(",");
  IElementType DOUBLE_QUOTE = new HJsonTokenType("\"");
  IElementType DOUBLE_QUOTED_STRING = new HJsonTokenType("DOUBLE_QUOTED_STRING");
  IElementType FALSE = new HJsonTokenType("false");
  IElementType LINE_COMMENT = new HJsonTokenType("LINE_COMMENT");
  IElementType L_BRACKET = new HJsonTokenType("[");
  IElementType L_CURLY = new HJsonTokenType("{");
  IElementType MULTILINE_STRING_TOKEN = new HJsonTokenType("MULTILINE_STRING_TOKEN");
  IElementType NULL = new HJsonTokenType("null");
  IElementType NUMBER = new HJsonTokenType("NUMBER");
  IElementType QUOTELESS_STRING = new HJsonTokenType("QUOTELESS_STRING");
  IElementType R_BRACKET = new HJsonTokenType("]");
  IElementType R_CURLY = new HJsonTokenType("}");
  IElementType SINGLE_QUOTE = new HJsonTokenType("'");
  IElementType SINGLE_QUOTED_STRING = new HJsonTokenType("SINGLE_QUOTED_STRING");
  IElementType TRUE = new HJsonTokenType("true");

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
      else if (type == MEMBER_NAME) {
        return new HJsonMemberNameImpl(node);
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
      else if (type == QUOTE_LESS_PART_STRING) {
        return new HJsonQuoteLessPartStringImpl(node);
      }
      else if (type == QUOTE_LESS_STRING) {
        return new HJsonQuoteLessStringImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
