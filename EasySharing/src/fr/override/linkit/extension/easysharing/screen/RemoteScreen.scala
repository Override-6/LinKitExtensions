package fr.`override`.linkit.`extension`.easysharing.screen

import fr.`override`.linkit.api.`extension`.fragment.RemoteFragment
import fr.`override`.linkit.api.packet.fundamental.EmptyPacket
import fr.`override`.linkit.api.packet.{DedicatedPacketCoordinates, Packet}
import org.bytedeco.javacv.FFmpegFrameGrabber

import scala.collection.mutable

class RemoteScreen extends RemoteFragment {
    override val nameIdentifier: String = "RemoteFragment"
    @volatile private var viewers = mutable.HashSet.empty[String]
    private val listeningScreens = mutable.Map.empty[String, RemoteScreenViewer]

    override def handleRequest(packet: Packet, coords: DedicatedPacketCoordinates): Unit = {
        packet match {
            case EmptyPacket => viewers += coords.senderID
            case StreamPacket(frameBytes) => listeningScreens(coords.senderID).pushFrame(frameBytes)
        }
    }

    override def start(): Unit = {

    }

    override def destroy(): Unit = {
        packetSender().close()
    }

    private def startScreenRecorder(): Unit = new Thread(() => {
        val x = 0
        val y = 0
        val w = 1024
        val h = 1080 // specify the region of screen to grab
        val grabber = new FFmpegFrameGrabber(":0.0+" + x + "," + y)
        grabber.setFormat("x11grab")
        grabber.setImageWidth(w)
        grabber.setImageHeight(h)
        grabber.start()

        while (true) {
            val frameBuffer = grabber.grab().data
            packetSender().sendTo(StreamPacket(frameBuffer.array()), viewers.toSeq: _*)
        }
    }).start()

    startScreenRecorder()

    private case class StreamPacket(val stream: Array[Byte]) extends Packet

}
