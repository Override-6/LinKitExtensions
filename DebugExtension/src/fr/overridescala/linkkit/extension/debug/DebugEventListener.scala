package fr.overridescala.linkkit.`extension`.debug

import fr.overridescala.linkkit.api.packet.{Packet, PacketCoordinates}
import fr.overridescala.linkkit.api.system.event.EventListener

class DebugEventListener extends EventListener {

    override def onPacketReceived(packet: Packet, coordinates: PacketCoordinates): Unit = {
        println(s"Received packet : '$packet', with coordinates $coordinates")
    }

    override def onPacketSent(packet: Packet, coordinates: PacketCoordinates): Unit = {
        println(s"Sent packet : '$packet', to coordinates $coordinates")
    }

}
