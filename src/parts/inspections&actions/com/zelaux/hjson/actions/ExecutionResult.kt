/*
package com.zelaux.numberconverter.actions

sealed class ExecutionResult<out CONTENT_TYPE> {
    class Content<CONTENT_TYPE>(val value: CONTENT_TYPE) : ExecutionResult<CONTENT_TYPE>()
    companion object {

        */
/*@JvmStatic
        fun <T> empty():ExecutionResult<T> = Empty*//*

        @JvmField
        val empty: ExecutionResult<Nothing> = Empty

        @JvmField
        val nullContent = Content(null)

        @JvmStatic
        fun <T> empty(): ExecutionResult<T?> = empty

        @JvmStatic
        fun <T> nullContent(): ExecutionResult<T?> = nullContent
    }

    private object Empty : ExecutionResult<Nothing>()

    fun isEmpty() = this is Empty
    fun unwrapOrNull(): CONTENT_TYPE? = (this as? Content)?.value

    fun unwrapThrow():CONTENT_TYPE = unwrapOrNull()!!
}*/
