package fr.`override`.linkit.`extension`.cloud.sync


import fr.`override`.linkit.api.`extension`.packet.PacketFactory
import fr.`override`.linkit.api.packet.channel.PacketChannel
import fr.`override`.linkit.api.packet.{Packet, PacketUtils}
import fr.`override`.linkit.api.system.fsa.FileAdapter


case class FolderSyncPacket(order: String,
                            affectedPath: String,
                            content: Array[Byte]) extends Packet

object FolderSyncPacket extends PacketFactory[FolderSyncPacket] {

    def apply(order: String, affectedPath: FileAdapter, content: Array[Byte] = Array())(implicit channel: PacketChannel): FolderSyncPacket = {
        new FolderSyncPacket(order, affectedPath.toString, content)
    }

    private val Type = "[fsync]".getBytes()
    private val Affected = "<affected>".getBytes()
    private val Content = "<content>".getBytes()

    override def decompose(implicit packet: FolderSyncPacket): Array[Byte] = {
        val orderBytes = packet.order.getBytes()
        val affectedBytes = packet.affectedPath.getBytes()
        Type ++ orderBytes ++ Affected ++ affectedBytes ++ Content ++ packet.content
    }

    override def canTransform(implicit bytes: Array[Byte]): Boolean = {
        bytes.startsWith(Type)
    }

    override def build(implicit bytes: Array[Byte]): FolderSyncPacket = {
        val order = PacketUtils.cutString(Type, Affected)
        val affectedPath = PacketUtils.cutString(Affected, Content)
        val content = PacketUtils.cutEnd(Content)

        new FolderSyncPacket(order, affectedPath, content)
    }

    override val packetClass: Class[FolderSyncPacket] = classOf[FolderSyncPacket]
}
