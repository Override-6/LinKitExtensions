package fr.`override`.linkit.extension.easysharing.clipboard

import fr.`override`.linkit.api.network.{AbstractRemoteFragmentController, RemoteFragmentController}
import fr.`override`.linkit.api.packet.fundamental.RefPacket.{ArrayPacket, ObjectPacket, StringPacket}
import fr.`override`.linkit.api.packet.fundamental.WrappedPacket
import fr.`override`.linkit.api.packet.{Packet, PacketCoordinates}

import java.awt.datatransfer.{DataFlavor, UnsupportedFlavorException}
import java.awt.image.BufferedImage
import java.awt.{Image, Toolkit}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.util
import javax.imageio.ImageIO

class RemoteClipboardController(controller: RemoteFragmentController)
        extends AbstractRemoteFragmentController(controller) {

    private val ownClipboard = Toolkit.getDefaultToolkit.getSystemClipboard

    override def handleRequest(packet: Packet, coordinates: PacketCoordinates): Unit = ()

    def pasteCurrentImage(): Unit = {
        val image = ownClipboard.getData(DataFlavor.imageFlavor).asInstanceOf[Image]
        pasteImage(image)
    }

    def canPasteCurrentImage: Boolean = isSuccessFull {
        ownClipboard.getData(DataFlavor.imageFlavor)
    }

    def pasteImage(image: Image): Unit = {
        val buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB)

        val graphics = buffered.getGraphics
        graphics.drawImage(image, 0, 0, null)
        graphics.dispose()
        pasteImage(buffered)
    }

    def pasteImage(image: BufferedImage): Unit = {
        val out = new ByteArrayOutputStream()
        ImageIO.write(image, "png", out)
        sendRequest(WrappedPacket("paste/img", ObjectPacket(out.toByteArray)))
    }

    def pasteCurrentText(): Unit = {
        paste(ownClipboard.getData(DataFlavor.stringFlavor).asInstanceOf[String])
    }

    def canPasteCurrentText: Boolean = isSuccessFull {
        ownClipboard.getData(DataFlavor.stringFlavor)
    }

    def paste(text: String): Unit = {
        sendRequest(WrappedPacket("paste/text", StringPacket(text)))
    }

    def pasteCurrentFiles(): Unit = {
        val files = ownClipboard.getData(DataFlavor.javaFileListFlavor).asInstanceOf[util.List[File]]
        val paths = files.stream()
                .map(_.getAbsolutePath)
                .toArray.asInstanceOf[Array[String]]
        pasteFiles(paths: _*)
    }

    def canPasteCurrentFiles: Boolean = isSuccessFull {
        ownClipboard.getData(DataFlavor.javaFileListFlavor)
    }

    def pasteFiles(paths: String*): Unit = {
        sendRequest(WrappedPacket("paste/paths", ArrayPacket(paths.toArray)))
    }

    def getImage: BufferedImage = {
        controller.sendRequest(StringPacket("get/img"))
        val bytes = controller.nextResponse[ObjectPacket].casted

        val buffer = new ByteArrayInputStream(bytes)
        ImageIO.read(buffer)
    }

    def getText: String = {
        controller.sendRequest(StringPacket("get/text"))
        controller.nextResponse[StringPacket].value
    }

    def getFiles: Array[String] = {
        controller.sendRequest(StringPacket("get/paths"))
        controller.nextResponse[ArrayPacket[String]].value
    }

    private def isSuccessFull(action: => Unit): Boolean = {
        var success = false
        try {
            action
            success = true
        } catch {
            case _: UnsupportedFlavorException => success = true
        }
        success
    }

}
