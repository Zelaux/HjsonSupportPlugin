package com.zelaux.hjson.lexer;

import arc.struct.IntMap;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class CacheLexer implements FlexLexer {
//    public static final TokenEntry EOF=new TokenEntry(null,-1,-1,)
    final FlexLexer delegate;
    IntMap<TokenEntry> entries = new IntMap<>();
    int offset = 0;
    int index = 0;

    public CacheLexer(FlexLexer delegate) {
        this.delegate = delegate;
        reset();
    }

    @Override
    public void yybegin(int state) {
        delegate.yybegin(state);
    }

    @Override
    public int yystate() {
        if(!entries.isEmpty()){
            TokenEntry entry = entries.get(offset + index);
            if(entry!=null) return entry.state;
        }
        return delegate.yystate();
    }

    @Override
    public int getTokenStart() {
        if(!entries.isEmpty()){
            TokenEntry entry = entries.get(offset + index);
            if(entry!=null) return entry.start;
        }
        return delegate.getTokenStart();
    }

    @Override
    public int getTokenEnd() {
        if(!entries.isEmpty()){
            TokenEntry entry = entries.get(offset + index);
            if(entry!=null) return entry.end;
        }
        return delegate.getTokenEnd();
    }

    @Nullable
    public TokenEntry removeFirst() throws IOException {
        entries.remove(offset);
        if (entries.isEmpty()) {
            reset();
            return advanceEntry();
        } else {
            if (index > 0) {
                for (int i = 0; i < index; i++) {
                    entries.remove(i + offset);
                }
                if (entries.isEmpty()) {
                    return removeFirst();
                } else {
                    offset += index-1;
                    index = 0;
                }
            } else index= 0;

            offset++;
            TokenEntry entry = entries.get(offset);

            return entry;
        }
    }

    private static int initIndex() {
        return -1;
    }

    public void gotoOffset(int indexWithOffset) {
        if (offset <= indexWithOffset-1 && indexWithOffset-1 <= entries.size + 1 + offset || entries.containsKey(indexWithOffset)) {
            this.index = indexWithOffset - offset;
        } else {
            throw new IndexOutOfBoundsException(indexWithOffset + " out of bounds [" + offset + ", " + (entries.size + offset) + "+1]");
        }
    }
    public void gotoIndex(int indexRelative) {
        if (0 <= indexRelative && indexRelative <= entries.size + 1 + offset || entries.containsKey(offset+indexRelative)) {
            this.index = indexRelative;
        } else {
            throw new IndexOutOfBoundsException(indexRelative + " out of bounds [0, " + (entries.size) + "]");
        }
    }

    @Override
    public IElementType advance() throws IOException {
        TokenEntry entry = advanceEntry();
        if (entry == null) return null;
        return entry.token;
    }


    public TokenEntry advanceEntry() throws IOException {

        index++;
        int mapIndex = index + offset;
        TokenEntry entry = entries.get(mapIndex);
        if (entry == null) {
            IElementType type = delegate.advance();
            if (type == null) {
                index--;
                return null;
            }
            entry = new TokenEntry(type, delegate, mapIndex);
            entries.put(mapIndex, entry);
        }
        return entry;
    }

    public void reset() {
        offset = 0;
        index= initIndex();
        entries.clear();
    }

    @Override
    public void reset(CharSequence buf, int start, int end, int initialState) {
        delegate.reset(buf, start, end, initialState);
        reset();
    }


    public static class TokenEntry {
        public final IElementType token;
        public final int start, end, state, indexWithOffset;

        private TokenEntry(IElementType token, int start, int end, int state, int indexWithOffset) {
            this.token = token;
            this.start = start;
            this.end = end;
            this.state = state;
            this.indexWithOffset = indexWithOffset;
        }

        private TokenEntry(IElementType token, FlexLexer lexer, int indexWithOffset) {
            this(token, lexer.getTokenStart(), lexer.getTokenEnd(), lexer.yystate(), indexWithOffset);
        }

        private TokenEntry(FlexLexer lexer, int indexWithOffset) throws IOException {
            this(lexer.advance(), lexer, indexWithOffset);
        }

        @Override
        public String toString() {
            return "TokenEntry{" +
                    "token=" + token +
                    ", start=" + start +
                    ", end=" + end +
                    ", state=" + state +
                    ", indexWithOffset=" + indexWithOffset +
                    '}';
        }
    }
}
