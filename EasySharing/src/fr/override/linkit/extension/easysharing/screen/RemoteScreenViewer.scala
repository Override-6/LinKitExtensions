package fr.`override`.linkit.extension.easysharing.screen

import fr.`override`.linkit.api.packet.serialization.NumberSerializer.convertByteArray
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image

import java.io.ByteArrayInputStream

class RemoteScreenViewer extends Canvas {

    private val graphics = getGraphicsContext2D

    def pushFrame(bytes: Array[Int]): Unit = {
        val img = new Image(new ByteArrayInputStream(convertByteArray(bytes)))
        graphics.drawImage(img, 0, 0)
        println("Set !")
    }

}
