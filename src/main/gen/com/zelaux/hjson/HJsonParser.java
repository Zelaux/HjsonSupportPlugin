// This is a generated file. Not intended for manual editing.
package com.zelaux.hjson;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.zelaux.hjson.HJsonElementTypes.*;
import static com.zelaux.hjson.psi.HJsonParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class HJsonParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return hjson(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ARRAY, BOOLEAN_LITERAL, JSON_STRING, LITERAL,
      MEMBER_VALUE, MULTILINE_STRING, NULL_LITERAL, NUMBER_LITERAL,
      OBJECT, OBJECT_FULL, QUOTE_LESS_STRING, STRING_LITERAL,
      VALUE),
  };

  /* ********************************************************** */
  // '['( value ( COMMA? value)* COMMA? )? ']'{
  // //  recoverWhile = not_bracket_or_next_value
  //     //pin=1
  // }
  public static boolean array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && array_1(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    r = r && array_3(b, l + 1);
    exit_section_(b, m, ARRAY, r);
    return r;
  }

  // ( value ( COMMA? value)* COMMA? )?
  private static boolean array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1")) return false;
    array_1_0(b, l + 1);
    return true;
  }

  // value ( COMMA? value)* COMMA?
  private static boolean array_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && array_1_0_1(b, l + 1);
    r = r && array_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( COMMA? value)*
  private static boolean array_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!array_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA? value
  private static boolean array_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = array_1_0_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean array_1_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_1_0_0")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // COMMA?
  private static boolean array_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_1_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // {
  // //  recoverWhile = not_bracket_or_next_value
  //     //pin=1
  // }
  private static boolean array_3(PsiBuilder b, int l) {
    return true;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean boolean_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "boolean_literal")) return false;
    if (!nextTokenIs(b, "<boolean literal>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLEAN_LITERAL, "<boolean literal>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (LINE_COMMENT NEW_LINE) | BLOCK_COMMENT
  public static boolean comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment")) return false;
    if (!nextTokenIs(b, "<comment>", BLOCK_COMMENT, LINE_COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMENT, "<comment>");
    r = comment_0(b, l + 1);
    if (!r) r = consumeToken(b, BLOCK_COMMENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LINE_COMMENT NEW_LINE
  private static boolean comment_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LINE_COMMENT, NEW_LINE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // object_full|object
  static boolean hjson(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hjson")) return false;
    boolean r;
    r = object_full(b, l + 1);
    if (!r) r = object(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING
  public static boolean json_string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "json_string")) return false;
    if (!nextTokenIs(b, "<json string>", DOUBLE_QUOTED_STRING, SINGLE_QUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JSON_STRING, "<json string>");
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, SINGLE_QUOTED_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // number_literal|boolean_literal|null_literal|string_literal
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, LITERAL, "<literal>");
    r = number_literal(b, l + 1);
    if (!r) r = boolean_literal(b, l + 1);
    if (!r) r = null_literal(b, l + 1);
    if (!r) r = string_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // member_name  COLON member_value
  public static boolean member(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MEMBER, "<member>");
    r = member_name(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && member_value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // quote_less_string|json_string
  public static boolean member_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MEMBER_NAME, "<member name>");
    r = quote_less_string(b, l + 1);
    if (!r) r = json_string(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // value
  public static boolean member_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, MEMBER_VALUE, "<member value>");
    r = value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MULTILINE_STRING_TOKEN
  public static boolean multiline_string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiline_string")) return false;
    if (!nextTokenIs(b, MULTILINE_STRING_TOKEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MULTILINE_STRING_TOKEN);
    exit_section_(b, m, MULTILINE_STRING, r);
    return r;
  }

  /* ********************************************************** */
  // !('}'|value)
  static boolean not_brace_or_next_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not_brace_or_next_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !not_brace_or_next_value_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}'|value
  private static boolean not_brace_or_next_value_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not_brace_or_next_value_0")) return false;
    boolean r;
    r = consumeToken(b, R_CURLY);
    if (!r) r = value(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // !(']'|value)
  static boolean not_bracket_or_next_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not_bracket_or_next_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !not_bracket_or_next_value_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ']'|value
  private static boolean not_bracket_or_next_value_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "not_bracket_or_next_value_0")) return false;
    boolean r;
    r = consumeToken(b, R_BRACKET);
    if (!r) r = value(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NULL
  public static boolean null_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "null_literal")) return false;
    if (!nextTokenIs(b, NULL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NULL);
    exit_section_(b, m, NULL_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // NUMBER
  public static boolean number_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "number_literal")) return false;
    if (!nextTokenIs(b, NUMBER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NUMBER);
    exit_section_(b, m, NUMBER_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // (member (COMMA? member)* COMMA?)?
  public static boolean object(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object")) return false;
    Marker m = enter_section_(b, l, _NONE_, OBJECT, "<object>");
    object_0(b, l + 1);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // member (COMMA? member)* COMMA?
  private static boolean object_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = member(b, l + 1);
    r = r && object_0_1(b, l + 1);
    r = r && object_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA? member)*
  private static boolean object_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!object_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "object_0_1", c)) break;
    }
    return true;
  }

  // COMMA? member
  private static boolean object_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = object_0_1_0_0(b, l + 1);
    r = r && member(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean object_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_0_1_0_0")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  // COMMA?
  private static boolean object_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // '{' object? '}'
  public static boolean object_full(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_full")) return false;
    if (!nextTokenIs(b, L_CURLY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY);
    r = r && object_full_1(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, m, OBJECT_FULL, r);
    return r;
  }

  // object?
  private static boolean object_full_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "object_full_1")) return false;
    object(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // QUOTELESS_STRING
  public static boolean quote_less_string(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quote_less_string")) return false;
    if (!nextTokenIs(b, QUOTELESS_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUOTELESS_STRING);
    exit_section_(b, m, QUOTE_LESS_STRING, r);
    return r;
  }

  /* ********************************************************** */
  // COMMA?
  public static boolean separator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "separator")) return false;
    Marker m = enter_section_(b, l, _NONE_, SEPARATOR, "<separator>");
    consumeToken(b, COMMA);
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // json_string | quote_less_string|multiline_string
  public static boolean string_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STRING_LITERAL, "<string literal>");
    r = json_string(b, l + 1);
    if (!r) r = quote_less_string(b, l + 1);
    if (!r) r = multiline_string(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // array|object_full|literal
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, VALUE, "<value>");
    r = array(b, l + 1);
    if (!r) r = object_full(b, l + 1);
    if (!r) r = literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
