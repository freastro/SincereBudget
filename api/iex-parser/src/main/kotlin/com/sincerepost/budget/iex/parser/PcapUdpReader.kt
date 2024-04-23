package com.sincerepost.budget.iex.parser

import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.Channels

/**
 * Reads the data of UDP packets from a PCAP file.
 * @see <a href="https://www.ietf.org/archive/id/draft-tuexen-opsawg-pcapng-02.txt">spec</a>
 */
class PcapUdpReader(`in`: InputStream): AbstractIterator<ByteBuffer>(), AutoCloseable {

    companion object {
        private const val BLOCK_HEADER_SIZE = 8  // Block Type (4) + Block Total Length (4)
        private const val BLOCK_FOOTER_SIZE = 4  // Block Total Length (4)
    }

    private val buffer = ByteBuffer.allocate(8196).limit(0).order(ByteOrder.LITTLE_ENDIAN)
    private val channel = Channels.newChannel(`in`)

    override fun close() {
        channel.close()
    }

    override fun computeNext() {
        if (!channel.isOpen) {
            throw IOException("Reader is closed")
        }

        var next: ByteBuffer
        do {
            // Check for EOF
            if (buffer.remaining() < 12) {  // BLOCK_HEADER_SIZE (8) + Byte-Order Magic (4)
                buffer.compact()
                if (channel.read(buffer) == -1) {
                    return done()
                }
                buffer.flip()
            }

            // Read next block
            next = readNextBlock()
        } while (next.remaining() == 0)

        setNext(next)
    }

    /**
     * Reads the next PCAP block.
     */
    private fun readNextBlock(): ByteBuffer {
        // Read block header
        // Remaining buffer guaranteed by computeNext()
        val blockType = buffer.getInt()
        if (blockType == 0x0A0D0D0A && buffer.getInt(8) != 0x1A2B3C4D) {
            throw IOException("Invalid byte order")
        }

        // Read block content
        val blockLength = buffer.getInt() - BLOCK_HEADER_SIZE
        if (buffer.remaining() < blockLength) {
            buffer.compact()
            channel.read(buffer)
            buffer.flip()
        }

        // Parse block content
        val block = buffer.slice(buffer.position(), blockLength - BLOCK_FOOTER_SIZE)
            .order(buffer.order())
        buffer.position(buffer.position() + blockLength)  // advance to next block

        return when (blockType) {
            0x0A0D0D0A -> readPcapSectionHeader(block)
            1 -> readPcapInterfaceDescription(block)
            6 -> readPcapEnhancedPacket(block)
            else -> throw IOException("Unsupported block type: $blockType")
        }
    }

    /**
     * Parses the PCAP Enhanced Packet Block.
     */
    private fun readPcapEnhancedPacket(block: ByteBuffer): ByteBuffer {
        block.position(12)  // skip Interface ID (4) + High Timestamp (4) + Low Timestamp (4)

        // Get packet data
        val packetLength = block.getInt()
        val packet = block.slice(block.position() + 4, packetLength)  // skip Original Packet Length (4)

        // OSI Layer 3 (Ethernet II)
        packet.position(12)  // skip Destination MAC Address (6) + Source MAC Address (6)

        val etherType = packet.getShort().toInt()
        if (etherType != 0x0800) {
            throw IOException("Unsupported ether type: $etherType")
        }

        // OSI Layer 3 (IPv4)
        val ipHeader = packet.get()

        val ipVersion = ipHeader.toInt().shr(4)
        if (ipVersion != 4) {
            throw IOException("Unsupported IP version: $ipVersion")
        }

        val dataOffset = packet.position() + ipHeader.toInt().and(0x0F) * 4 - 1
        packet.position(packet.position() + 8)  // skip DSCP/ECN (1) + Total Length (2) + Identification (2) + Flags/Fragment-Offset (2) + Time To Live (1)

        val protocol = packet.get().toInt()
        if (protocol != 17) {
            throw IOException("Unsupported IP protocol: $protocol")
        }

        packet.position(dataOffset)

        // OSI Layer 4 (UDP)
        packet.position(packet.position() + 4)  // skip Source Port (2) + Destination Port (2)

        val dataLength = packet.getShort().toInt() - 8
        return packet.slice(packet.position() + 2, dataLength)  // skip Checksum (2)
            .order(ByteOrder.LITTLE_ENDIAN)
    }

    /**
     * Parses the PCAP Interface Description Block.
     */
    private fun readPcapInterfaceDescription(block: ByteBuffer): ByteBuffer {
        // Verify link type (Ethernet II)
        val linkType = block.getShort().toInt()
        if (linkType != 1) {
            throw IOException("Unsupported link type: $linkType")
        }

        // Verify maximum packet length
        val snapLen = block.position(4).getInt()
        if (snapLen + 28 + BLOCK_FOOTER_SIZE < buffer.capacity()) {  // Enhanced Packet Block header (28)
            throw IOException("Insufficient buffer capacity for SnapLen: $snapLen")
        }

        // No packet, return empty buffer
        return block.position(block.limit())
    }

    /**
     * Parses the PCAP Section Header Block.
     */
    private fun readPcapSectionHeader(block: ByteBuffer): ByteBuffer {
        block.position(4)  // skip Byte-Order Magic (4)

        // Verify PCAP version
        val majorVersion = block.getShort().toInt()
        val minorVersion = block.getShort().toInt()
        if (majorVersion != 1 && minorVersion != 0) {
            throw IOException("Unsupported PCAP version: $majorVersion.$minorVersion")
        }

        // No packet, return empty buffer
        return block.position(block.limit())
    }
}