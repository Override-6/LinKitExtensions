package fr.`override`.linkit.`extension`.cloud.sync

import fr.`override`.linkit.api.packet.channel.PacketChannel
import fr.`override`.linkit.api.packet.{Packet, PacketFactory, PacketTranslator, PacketUtils}
import fr.`override`.linkit.api.system.fsa.FileAdapter


case class FolderSyncPacket(order: String,
                            affectedPath: String,
                            content: Array[Byte]) extends Packet

object FolderSyncPacket extends PacketFactory[FolderSyncPacket] {

    override val packetClass: Class[FolderSyncPacket] = classOf[FolderSyncPacket]
    private val Type = "[fsync]".getBytes()
    private val Affected = "<affected>".getBytes()
    private val Content = "<content>".getBytes()

    def apply(order: String, affectedPath: FileAdapter, content: Array[Byte] = Array())(implicit channel: PacketChannel): FolderSyncPacket = {
        new FolderSyncPacket(order, affectedPath.toString, content)
    }

    override def decompose(translator: PacketTranslator)(implicit packet: FolderSyncPacket): Array[Byte] = {
        val orderBytes = packet.order.getBytes()
        val affectedBytes = packet.affectedPath.getBytes()
        Type ++ orderBytes ++ Affected ++ affectedBytes ++ Content ++ packet.content
    }

    override def canTransform(translator: PacketTranslator)(implicit bytes: Array[Byte]): Boolean = {
        bytes.startsWith(Type)
    }

    override def build(translator: PacketTranslator)(implicit bytes: Array[Byte]): FolderSyncPacket = {
        val order = PacketUtils.stringBetween(Type, Affected)
        val affectedPath = PacketUtils.stringBetween(Affected, Content)
        val content = PacketUtils.untilEnd(Content)

        new FolderSyncPacket(order, affectedPath, content)
    }
}
