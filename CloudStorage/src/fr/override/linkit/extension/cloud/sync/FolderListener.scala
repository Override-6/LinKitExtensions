package fr.`override`.linkit.`extension`.cloud.sync


import java.io.IOException

import fr.`override`.linkit.api.packet.channel.PacketChannel
import fr.`override`.linkit.api.system.fsa.{FileAdapter, FileSystemAdapter}

class FolderListener(implicit channel: PacketChannel, fsa: FileSystemAdapter) {

    def onRename(affected: FileAdapter, newName: String): Unit = {
        channel.sendPacket(FolderSyncPacket("rename", affected, newName.getBytes()))
    }

    def onDelete(affected: FileAdapter): Unit = {
        println("DELETED " + affected)
        channel.sendPacket(FolderSyncPacket("delete", affected))
    }

    def onModify(affected: FileAdapter): Unit = {
        if (affected isDirectory) {
            channel.sendPacket(FolderSyncPacket("mkdirs", affected))
            return
        }

        uploadDirectory(affected)
    }

    private def uploadDirectory(affected: FileAdapter): Unit = {
        if (affected.isDirectory || affected.notExists)
            return
        try {
            val bytes = fsa.readAllBytes(affected)
            println(s"bytes.length = ${bytes.length}")
            channel.sendPacket(FolderSyncPacket(s"upload", affected, bytes))
        } catch {
            case e: IOException =>
                Console.err.println(e.getMessage)
                Thread.sleep(200)
                onModify(affected)
        }
    }

}
