package fr.`override`.linkit.`extension`.easysharing.screen

import com.googlecode.javacv.FFmpegFrameGrabber
import fr.`override`.linkit.api.`extension`.fragment.RemoteFragment
import fr.`override`.linkit.api.packet.{Packet, PacketCoordinates}

class RemoteScreen extends RemoteFragment {
    override val nameIdentifier: String = "RemoteFragment"
    @volatile private var shareScreen = false

    override def handleRequest(packet: Packet, coords: PacketCoordinates): Unit = {
        packet match {
            case
        }
    }

    override def start(): Unit = {

    }

    override def destroy(): Unit = {
        packetSender().close()
    }

    def

    private def shareScreenWith(targetID: String): Unit = {
        shareScreen = true

        val x = 0
        val y = 0
        val w = 1024
        val h = 768 // specify the region of screen to grab
        val grabber = new FFmpegFrameGrabber(":0.0+" + x + "," + y)
        grabber.setFormat("x11grab")
        grabber.setImageWidth(w)
        grabber.setImageHeight(h)
        grabber.start()

        while (true) {
            val frameBuffer = grabber.grab().getByteBuffer
            packetSender().sendTo(targetID, new StreamPacket(frameBuffer.array()))
        }
    }

    private class StreamPacket(packet: Array[Byte]) extends Packet {

    }

}
