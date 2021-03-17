package fr.`override`.linkit.extension.debug.tests

import fr.`override`.linkit.skull.Relay
import fr.`override`.linkit.skull.internal.plugin.fragment.RemoteFragment
import fr.`override`.linkit.skull.connection.packet.fundamental.RefPacket.ObjectPacket
import fr.`override`.linkit.skull.connection.packet.Packet

class TestRemoteFragment(relay: Relay) extends RemoteFragment() {
    override val nameIdentifier: String = "Test Remote Fragment " + relay.identifier

    override def handleRequest(packet: Packet, coords: DedicatedPacketCoordinates): Unit = {
        packetSender().sendTo(ObjectPacket("I Received your request !"), coords.senderID)
    }

    override def start(): Unit = {
    }

    override def destroy(): Unit = {
    }
}
