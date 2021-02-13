package fr.`override`.linkit.`extension`.easysharing.screen

import java.io.ByteArrayInputStream

import javafx.scene.canvas.Canvas
import javafx.scene.image.Image

class RemoteScreenViewer extends Canvas {

    private val graphics = getGraphicsContext2D

    def pushFrame(bytes: Array[Byte]): Unit = {
        val img = new Image(new ByteArrayInputStream(bytes))
        graphics.drawImage(img, 0, 0)
        println("Set !")
    }

}
