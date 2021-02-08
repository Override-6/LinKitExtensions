package fr.`override`.linkit.`extension`.debug.tests

import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.fragment.RemoteFragment
import fr.`override`.linkit.api.packet.fundamental.ValPacket
import fr.`override`.linkit.api.packet.{Packet, PacketCoordinates}

class TestRemoteFragment(relay: Relay) extends RemoteFragment() {
    override val nameIdentifier: String = "Test Remote Fragment " + relay.identifier

    //TODO create a special packet channel that handles remote fragments
    override def handleRequest(packet: Packet, coords: PacketCoordinates): Unit = {
        println(s"packet = ${packet}")
        packetSender().sendTo(coords.senderID, ValPacket("I Received your request !"))
    }

    override def start(): Unit = {
    }

    override def destroy(): Unit = {
    }
}
