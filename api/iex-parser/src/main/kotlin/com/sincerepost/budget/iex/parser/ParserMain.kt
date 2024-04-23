package com.sincerepost.budget.iex.parser

import java.io.FileInputStream
import java.util.zip.GZIPInputStream
import kotlin.system.exitProcess

object ParserMain {

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 1) {
            println("usage: ParserMain <file>")
            exitProcess(1)
        }

        read(args[0]) {
            while (it.hasNext()) {
                println(it.next())
            }
        }
    }

    /**
     * Reads the IEX TOPS history file at the given path.
     */
    private fun read(path: String, block: (IexTopsReader) -> Unit) {
        FileInputStream(path).use { file ->
            GZIPInputStream(file).use { gzip ->
                PcapUdpReader(gzip).use { pcap ->
                    IexTopsReader(pcap).use(block)
                }
            }
        }
    }
}