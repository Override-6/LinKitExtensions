package fr.`override`.linkit.`extension`.easysharing.clipboard

import java.awt.datatransfer._
import java.awt.image.BufferedImage
import java.awt.{Image, Toolkit}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.util

import fr.`override`.linkit.api.`extension`.fragment.RemoteFragment
import fr.`override`.linkit.api.network.cache.ObjectPacket
import fr.`override`.linkit.api.packet.Packet
import fr.`override`.linkit.api.packet.channel.CommunicationPacketChannel
import fr.`override`.linkit.api.utils.WrappedPacket
import javax.imageio.ImageIO

class RemoteClipboard extends RemoteFragment with ClipboardOwner {
    override val nameIdentifier: String = "RemoteClipboard"
    private val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard

    override def handleRequest(packet: Packet, responseChannel: CommunicationPacketChannel): Unit = {
        packet match {
            case WrappedPacket("paste/text", ObjectPacket(text: String)) =>
                val transferableText = new StringSelection(text)
                clipboard.setContents(transferableText, this)

            case WrappedPacket("paste/img", ObjectPacket(bytes: Array[Byte])) =>
                val buffer = new ByteArrayInputStream(bytes)
                val buffImage = ImageIO.read(buffer)
                val transferableImage = new TransferableImage(buffImage)
                clipboard.setContents(transferableImage, this)

            case WrappedPacket("paste/paths", ObjectPacket(paths: String)) =>
                throw new UnsupportedOperationException("Not implemented yet.")

            case ObjectPacket("get/text") =>
                val data = clipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String]
                responseChannel.sendResponse(ObjectPacket(data))

            case ObjectPacket("get/img") =>
                val bytes = currentImageBytes
                responseChannel.sendResponse(ObjectPacket(bytes))

            case ObjectPacket("get/paths") =>
                val files = clipboard.getData(DataFlavor.javaFileListFlavor).asInstanceOf[util.List[File]]
                val paths = files.stream()
                        .map(_.getAbsolutePath)
                        .toArray
                responseChannel.sendResponse(ObjectPacket(paths))
        }
    }

    def currentImageBytes: Array[Byte] = {
        val image = clipboard.getData(DataFlavor.imageFlavor).asInstanceOf[Image]
        val buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)

        val graphics = buffered.getGraphics
        graphics.drawImage(image, 0,0, null)
        graphics.dispose()

        val out = new ByteArrayOutputStream()
        ImageIO.write(buffered, "png", out)
        out.toByteArray
    }

    override def start(): Unit = ()

    override def destroy(): Unit = ()

    override def lostOwnership(clipboard: Clipboard, contents: Transferable): Unit = ()

    private class TransferableImage(image: Image) extends Transferable {
        override def isDataFlavorSupported(flavor: DataFlavor): Boolean = getTransferDataFlavors.contains(flavor)

        override def getTransferDataFlavors: Array[DataFlavor] = Array(DataFlavor.imageFlavor)

        override def getTransferData(flavor: DataFlavor): AnyRef = {
            if (flavor.equals(DataFlavor.imageFlavor) && image != null)
                image
            else throw new UnsupportedFlavorException(flavor)
        }
    }

}
