package fr.`override`.linkit.`extension`.debug.tests

import fr.`override`.linkit.api.`extension`.fragment.RemoteFragment
import fr.`override`.linkit.api.packet.Packet
import fr.`override`.linkit.api.packet.channel.PacketChannel
import fr.`override`.linkit.api.packet.fundamental.DataPacket

class TestRemoteFragment extends RemoteFragment {
    override val nameIdentifier: String = "Test Remote Fragment"

    override def handleRequest(packet: Packet, responseChannel: PacketChannel): Unit = {
        println(s"packet = ${packet}")
        responseChannel.sendPacket(DataPacket("I Received your request !"))
    }

    override def start(): Unit = {
        println("START")
    }

    override def destroy(): Unit = {
        println("DESTROYED")
    }
}
