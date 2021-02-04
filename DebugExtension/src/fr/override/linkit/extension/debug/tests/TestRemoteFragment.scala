package fr.`override`.linkit.`extension`.debug.tests

import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.fragment.RemoteFragment
import fr.`override`.linkit.api.packet.Packet
import fr.`override`.linkit.api.packet.fundamental.DataPacket
import fr.`override`.linkit.api.packet.traffic.dedicated.CommunicationPacketChannel

class TestRemoteFragment(relay: Relay) extends RemoteFragment {
    override val nameIdentifier: String = "Test Remote Fragment " + relay.identifier

    //TODO create a special packet channel that handles remote fragments
    override def handleRequest(packet: Packet, responseChannel: CommunicationPacketChannel): Unit = {
        println(s"packet = ${packet}")
        responseChannel.sendResponse(DataPacket("I Received your request !"))
    }

    override def start(): Unit = {
    }

    override def destroy(): Unit = {
    }
}
