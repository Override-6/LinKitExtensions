package fr.`override`.linkit.extension.debug.tests

import java.util.concurrent.ThreadLocalRandom

import fr.`override`.linkit.api.connection.packet.{DedicatedPacketCoordinates, Packet}
import fr.`override`.linkit.api.local.plugin.fragment.RemoteFragment
import fr.`override`.linkit.core.connection.packet.fundamental.RefPacket.ObjectPacket

/*class TestRemoteFragment extends RemoteFragment {
    override val nameIdentifier: String = "Test Remote Fragment " + ThreadLocalRandom.current().nextInt()

    override def handleRequest(packet: Packet, coords: DedicatedPacketCoordinates): Unit = {
        packetSender().sendTo(ObjectPacket("I Received your request !"), coords.senderID)
    }

    override def start(): Unit = {
    }

    override def destroy(): Unit = {
    }
 */
