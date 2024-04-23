package com.sincerepost.budget.iex.parser

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Reads Official Price Messages from TOPS data of an IEX-TS stream.
 * @see <a href="https://www.iexexchange.io/documents/iex-tp-v1">IEX-TP v1</a>
 * @see <a href="https://www.iexexchange.io/documents/tops-v1-66">TOPS v1.66</a>
 */
class IexTopsReader(private val input: PcapUdpReader): AbstractIterator<OfficialPriceMessage>(), AutoCloseable {

    private var segment = ByteBuffer.allocate(0)

    override fun close() {
        input.close()
    }

    override fun computeNext() {
        var next: ByteBuffer? = null

        while (next == null) {
            if (segment.remaining() == 0) {
                // Read the next IEX-TS segment
                if (!input.hasNext()) {
                    return done()
                }
                segment = input.next().order(ByteOrder.LITTLE_ENDIAN)
                readIexTpHeader()
            } else {
                // Read the next TOPS message
                val len = segment.getShort().toInt()
                val offset = segment.position()

                val type = segment.get().toInt()
                if (type == 0x58) {
                    next = segment.slice(offset, len).order(ByteOrder.LITTLE_ENDIAN)
                }
                segment.position(offset + len)
            }
        }

        setNext(OfficialPriceMessage.fromBytes(next))
    }

    /**
     * Parses an IEX-TP header.
     */
    private fun readIexTpHeader() {
        val version = segment.get().toInt()
        if (version != 1) {
            throw IOException("Unsupported IEX-TP version: $version")
        }

        val protocol = segment.position(2).getShort().toUShort().toInt()
        if (protocol != 0x8003) {
            throw IOException("Unsupported IEX-TP protocol: $protocol")
        }

        segment.position(40)
    }
}