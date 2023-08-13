package com.zelaux.hjson;

import com.intellij.lang.Language;
import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.StringLiteralLexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.StringEscapesTokenTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.json.highlighting.JsonSyntaxHighlighterFactory.*;

public class HJsonSyntaxHighlighterFactory extends SyntaxHighlighterFactory {

    private static final String PERMISSIVE_ESCAPES;

    static {
        final StringBuilder escapesBuilder = new StringBuilder("/");
        for (char c = '\1'; c < '\255'; c++) {
            if (c != 'x' && c != 'u' && !Character.isDigit(c) && c != '\n' && c != '\r') {
                escapesBuilder.append(c);
            }
        }
        PERMISSIVE_ESCAPES = escapesBuilder.toString();
    }


    @NotNull
    @Override
    public SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        return new MyHighlighter(virtualFile);
    }

    @NotNull
    protected Lexer getLexer() {
        return new HJsonLexer();
    }

    protected boolean isCanEscapeEol() {
        return false;
    }

    private class MyHighlighter extends SyntaxHighlighterBase {
        private final Map<IElementType, TextAttributesKey> ourAttributes = new HashMap<>();

        @Nullable
        private final VirtualFile myFile;

        {
            fillMap(ourAttributes, JSON_BRACES, HJsonElementTypes.L_CURLY, HJsonElementTypes.R_CURLY);
            fillMap(ourAttributes, JSON_BRACKETS, HJsonElementTypes.L_BRACKET, HJsonElementTypes.R_BRACKET);
            fillMap(ourAttributes, JSON_COMMA, HJsonElementTypes.COMMA);
            fillMap(ourAttributes, JSON_COLON, HJsonElementTypes.COLON);
            fillMap(ourAttributes, JSON_STRING,
                    HJsonElementTypes.MULTILINE_STRING_TOKEN,
                    HJsonElementTypes.SINGLE_QUOTED_STRING,
                    HJsonElementTypes.DOUBLE_QUOTED_STRING,
                    HJsonElementTypes.QUOTELESS_STRING
            );
            fillMap(ourAttributes, JSON_PARAMETER, HJsonElementTypes.MEMBER_NAME);
            fillMap(ourAttributes, JSON_NUMBER, HJsonElementTypes.NUMBER);
            fillMap(ourAttributes, JSON_KEYWORD, HJsonElementTypes.TRUE, HJsonElementTypes.FALSE, HJsonElementTypes.NULL);
            fillMap(ourAttributes, JSON_LINE_COMMENT, HJsonElementTypes.LINE_COMMENT);
            fillMap(ourAttributes, JSON_BLOCK_COMMENT, HJsonElementTypes.BLOCK_COMMENT);
            // TODO may be it's worth to add more sensible highlighting for identifiers
//            fillMap(ourAttributes, JSON_IDENTIFIER, HJsonElementTypes.IDENTIFIER);
            fillMap(ourAttributes, HighlighterColors.BAD_CHARACTER, TokenType.BAD_CHARACTER);

            fillMap(ourAttributes, JSON_VALID_ESCAPE, StringEscapesTokenTypes.VALID_STRING_ESCAPE_TOKEN);
            fillMap(ourAttributes, JSON_INVALID_ESCAPE, StringEscapesTokenTypes.INVALID_CHARACTER_ESCAPE_TOKEN);
            fillMap(ourAttributes, JSON_INVALID_ESCAPE, StringEscapesTokenTypes.INVALID_UNICODE_ESCAPE_TOKEN);
        }

        MyHighlighter(@Nullable VirtualFile file) {
            myFile = file;
        }

        @NotNull
        @Override
        public Lexer getHighlightingLexer() {
            LayeredLexer layeredLexer = new LayeredLexer(getLexer());
            boolean isPermissiveDialect = isPermissiveDialect();
            layeredLexer.registerSelfStoppingLayer(new StringLiteralLexer('\"', HJsonElementTypes.DOUBLE_QUOTED_STRING, isCanEscapeEol(),
                                                           isPermissiveDialect ? PERMISSIVE_ESCAPES : "/", false, isPermissiveDialect) {
                                                       @NotNull
                                                       @Override
                                                       protected IElementType handleSingleSlashEscapeSequence() {
                                                           return isPermissiveDialect ? myOriginalLiteralToken : super.handleSingleSlashEscapeSequence();
                                                       }

                                                       @Override
                                                       protected boolean shouldAllowSlashZero() {
                                                           return isPermissiveDialect;
                                                       }
                                                   },
                    new IElementType[]{HJsonElementTypes.DOUBLE_QUOTED_STRING}, IElementType.EMPTY_ARRAY);
            layeredLexer.registerSelfStoppingLayer(new StringLiteralLexer('\'', HJsonElementTypes.SINGLE_QUOTED_STRING, isCanEscapeEol(),
                                                           isPermissiveDialect ? PERMISSIVE_ESCAPES : "/", false, isPermissiveDialect){
                                                       @NotNull
                                                       @Override
                                                       protected IElementType handleSingleSlashEscapeSequence() {
                                                           return isPermissiveDialect ? myOriginalLiteralToken : super.handleSingleSlashEscapeSequence();
                                                       }

                                                       @Override
                                                       protected boolean shouldAllowSlashZero() {
                                                           return isPermissiveDialect;
                                                       }

                                                       @Override
                                                       public IElementType getTokenType() {
                                                           return super.getTokenType();
                                                       }
                                                   },
                    new IElementType[]{HJsonElementTypes.SINGLE_QUOTED_STRING}, IElementType.EMPTY_ARRAY);
            return layeredLexer;
        }

        private boolean isPermissiveDialect() {
            FileType fileType = myFile == null ? null : myFile.getFileType();
            boolean isPermissiveDialect = false;
            if (fileType instanceof HJsonFileType) {
                Language language = ((HJsonFileType) fileType).getLanguage();
                isPermissiveDialect = language instanceof HJsonLanguage && ((HJsonLanguage) language).hasPermissiveStrings();
            }
            return isPermissiveDialect;
        }

        @Override
        public TextAttributesKey @NotNull [] getTokenHighlights(IElementType type) {
            return pack(ourAttributes.get(type));
        }
    }
}
