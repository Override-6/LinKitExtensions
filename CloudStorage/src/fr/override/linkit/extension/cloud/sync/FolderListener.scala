package fr.`override`.linkit.`extension`.cloud.sync

import java.nio.file.{FileSystemException, Files, Path}

import fr.`override`.linkit.api.packet.channel.PacketChannel

class FolderListener(implicit channel: PacketChannel) {

    def onRename(affected: Path, newName: String): Unit = {
        channel.sendPacket(FolderSyncPacket("rename", affected, newName.getBytes()))
    }

    def onDelete(affected: Path): Unit = {
        println("DELETED " + affected)
        channel.sendPacket(FolderSyncPacket("delete", affected))
    }

    def onModify(affected: Path): Unit = {
        if (Files.isDirectory(affected)) {
            channel.sendPacket(FolderSyncPacket("mkdirs", affected))
            return
        }

        uploadDirectory(affected)
    }

    private def uploadDirectory(affected: Path): Unit = {
        if (Files.isDirectory(affected) || Files.notExists(affected))
            return
        try {
            val bytes = Files.readAllBytes(affected)
            println(s"bytes.length = ${bytes.length}")
            channel.sendPacket(FolderSyncPacket(s"upload", affected, bytes))
        } catch {
            case e: FileSystemException =>
                Console.err.println(e.getMessage)
                Thread.sleep(200)
                onModify(affected)
        }
    }

}
