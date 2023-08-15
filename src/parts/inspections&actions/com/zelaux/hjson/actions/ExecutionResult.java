package com.zelaux.hjson.actions;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public abstract class ExecutionResult<CONTENT_TYPE> {
    @SuppressWarnings("rawtypes")
    public static final ExecutionResult Empty = new ExecutionResult() {
        @Nullable
        @Override
        public Object unwrapOrNull() {
            return null;
        }

        @Override
        public Object unwrapOrThrow() {
            throw new IllegalArgumentException("No value present");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };
    @SuppressWarnings("rawtypes")
    public static final Content nullContent=new Content(null);
    public static <T> ExecutionResult<@Nullable T> nullContent(){
        return ExecutionResult.nullContent;
    }
    public static <T> ExecutionResult<T> empty(){
        return ExecutionResult.Empty;
    }

    private ExecutionResult() {

    }

    @Nullable
    public abstract CONTENT_TYPE unwrapOrNull();

    public abstract CONTENT_TYPE unwrapOrThrow();

    public abstract boolean isEmpty();

    public static class Content<CONTENT_TYPE> extends ExecutionResult<CONTENT_TYPE> {
        public final CONTENT_TYPE value;

        public Content(CONTENT_TYPE value) {
            this.value = value;
        }

        @Override
        public @Nullable CONTENT_TYPE unwrapOrNull() {
            return value;
        }

        @Override
        public CONTENT_TYPE unwrapOrThrow() {
            return value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
