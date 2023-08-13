/*
package com.zelaux.arcplugin.settings.processor

import java.io.PrintStream


open class IndentStream
@JvmOverloads
constructor(printStream: PrintStream, indentValue: String = "\t") : PrintStream(printStream) {
    private var indentAmount = 0;

    @JvmField
    var separator: String = System.lineSeparator()
    private fun increaseIndent() {
        indentAmount++;
        if(indentBytes.size*indentAmount>byteSeq.size){
            byteSeq= ByteArray(((indentAmount*1.75).toInt()*indentBytes.size)){
                indentBytes[it%indentBytes.size]
            }
        }
    }


    private fun decreaseIndent() {
        if (indentAmount == 0) throw IllegalArgumentException("Nothing to decrease");
        indentAmount--

    }

    fun indent(block: () -> Unit) {
        increaseIndent()
        try {
            block()
        } finally {
            decreaseIndent()
        }
    }

    fun indent(block: Runnable) = indent(block::run)

    private var wasNextLine = false;
    override fun write(buf: ByteArray, off: Int, len: Int) {
        var prevIndex = 0;
        if (wasNextLine) {
            super.write(byteSeq, 0, indentAmount)
            wasNextLine = false;
        }
        for (i in off until len) {
            var placeIndent = false
            if (buf[i] == systemSeparatorBytes.last() && i - systemSeparatorBytes.lastIndex >= 0) {
                placeIndent = true
                for (j in systemSeparatorBytes.indices) {
                    if (buf[i - systemSeparatorBytes.lastIndex + j] != systemSeparatorBytes[j]) {
                        placeIndent = false
                        break
                    }
                }

            }
            if (buf[i] == nextLineByte && i - nextLineIndex >= 0) {
                for (j in systemSeparatorBytes.indices) {
                    if (buf[i - nextLineIndex + j] != systemSeparatorBytes[j]) {
                        placeIndent = true
                        break
                    }
                }
            }
            if (placeIndent) {
                super.write(buf, off + prevIndex, i - prevIndex + 1)
                if (i < len - 1) {
                    super.write(byteSeq, 0, indentAmount * indentBytes.size)
                } else {
                    wasNextLine = true
                }

                prevIndex = i + 1
            }
        }
        super.write(buf, off + prevIndex, len - prevIndex)

    }

    override fun println() {
        newLine()
    }

    override fun println(x: Boolean) {
        super.print(x)
        newLine()
    }

    override fun println(x: Char) {
        super.print(x)
        newLine()
    }

    override fun println(x: Int) {
        super.print(x)
        newLine()
    }

    override fun println(x: Long) {
        super.print(x)
        newLine()
    }

    override fun println(x: Float) {
        super.print(x)
        newLine()
    }

    override fun println(x: Double) {
        super.print(x)
        newLine()
    }

    override fun println(x: CharArray) {
        super.print(x)
        newLine()
    }

    override fun println(x: String?) {
        super.print(x)
        newLine()
    }

    override fun println(x: Any?) {
        super.print(x)
        newLine()
    }

    fun newLine() {
        super.print(separator)
    }
@JvmField
    val indentBytes = indentValue.toByteArray()
    @get:JvmName("byteSeq")
    @set:JvmName("byteSeq")
    var byteSeq = ByteArray(indentBytes.size * 8) {
        indentBytes[it % indentBytes.size]
    }
        private set

    companion object {
        @JvmField
        val systemSeparatorBytes = System.lineSeparator().toByteArray()
        const val nextLineByte = '\n'.code.toByte()
        @JvmField
        val nextLineIndex = systemSeparatorBytes.indexOf(nextLineByte)

    }

}*/
