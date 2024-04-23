package com.sincerepost.budget.iex.parser

import java.nio.ByteBuffer

/**
 * An Official Price Message for an IEX-listed security.
 */
data class OfficialPriceMessage(val type: PriceType, val timestamp: Long, val symbol: String, val price: Long) {

    enum class PriceType {
        OPENING,
        CLOSING
    }

    companion object {
        /**
         * Parses an `OfficialPriceMessage` from the given bytes.
         */
        fun fromBytes(bytes: ByteBuffer): OfficialPriceMessage {
            val msgType = bytes.get().toInt()
            if (msgType != 0x58) {
                throw IllegalArgumentException("Unsupported message type: $msgType")
            }

            val priceType = when (val value = bytes.get().toInt()) {
                0x51 -> PriceType.OPENING
                0x4D -> PriceType.CLOSING
                else -> throw IllegalArgumentException("Unsupported price type: $value")
            }

            val timestamp = bytes.getLong()

            val symbolAscii = ByteArray(8)
            bytes.get(symbolAscii)

            val price = bytes.getLong()

            return OfficialPriceMessage(priceType, timestamp, String(symbolAscii).stripTrailing(), price)
        }
    }
}