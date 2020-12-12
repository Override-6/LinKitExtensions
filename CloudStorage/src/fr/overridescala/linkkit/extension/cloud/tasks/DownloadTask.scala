package fr.overridescala.linkkit.`extension`.cloud.tasks

import java.io.File
import java.nio.file.{Files, Path}

import fr.overridescala.linkkit.`extension`.cloud.transfer.TransferDescription
import DownloadTask.TYPE
import fr.overridescala.linkkit.api.exception.TaskException
import fr.overridescala.linkkit.api.packet.Packet
import fr.overridescala.linkkit.api.packet.fundamental.{DataPacket, ErrorPacket}
import fr.overridescala.linkkit.api.task.{Task, TaskInitInfo}
import fr.overridescala.linkkit.api.utils.Utils

/**
 * Downloads a File or folder from a targeted Relay
 *
 * @param desc the description about this transfer
 * @see [[TransferDescription]]
 * */
class DownloadTask private(private val desc: TransferDescription)
        extends Task[Unit](desc.targetID) {

    private var totalBytesWritten = 0

    override val initInfo: TaskInitInfo =
        TaskInitInfo.of(TYPE, desc.targetID, Utils.serialize(desc))

    override def execute(): Unit = {
        val response = nextPacket(): DataPacket
        //empty upload check
        if (response.header == UploadTask.END_OF_TRANSFER) {
            success()
            return
        }
        val downloadPath = findDownloadPath(response)
        try {
            downloadFile(downloadPath)
        } catch {
            case e: Throwable =>
                e.printStackTrace()
                val typeName = e.getClass.getCanonicalName
                val errMsg = e.getMessage
                var msg = s"$typeName : $errMsg"
                if (errMsg == null)
                    msg = s"got an error of type : $typeName"
                channel.sendPacket(ErrorPacket(typeName, msg))
                fail(msg)
        }
    }


    private def downloadFile(downloadPath: Path): Unit = {
        println(s"DOWNLOAD START $downloadPath")
        if (checkPath(downloadPath))
            return
        val stream = Files.newOutputStream(downloadPath)
        var count = 0
        var packet = nextPacket(): DataPacket

        def downloading: Boolean = packet.header != UploadTask.UPLOAD_FILE && packet.header != UploadTask.END_OF_TRANSFER

        while (downloading) {
            totalBytesWritten += packet.content.length
            stream.write(packet.content)
            count += 1
            packet = nextPacket(): DataPacket
            println(s"received = $totalBytesWritten, packets exchange = $count")
        }
        println()
        stream.close()
        handleLastTransferResponse(packet)
    }

    private def findDownloadPath(packet: DataPacket): Path = {
        Utils.checkPacketHeader(packet, Array("UPF"))
        val source = Utils.formatPath(desc.source).toString
        val root = Utils.formatPath(source.substring(0, source.lastIndexOf(File.separatorChar)))
        val rootNameCount = root.toString.count(_ == File.separatorChar)

        val uploadedFile = Utils.formatPath(new String(packet.content))
        val destination = Utils.formatPath(new String(desc.destination))

        val relativePath = Utils.subPathOfUnknownFile(uploadedFile, rootNameCount)
        Utils.formatPath(destination.toString + relativePath)
    }


    private def handleLastTransferResponse(packet: DataPacket): Unit = {
        val header = packet.header
        Utils.checkPacketHeader(packet, Array(UploadTask.END_OF_TRANSFER, UploadTask.UPLOAD_FILE))
        if (header.equals(UploadTask.END_OF_TRANSFER))
            success()
        else if (header.equals(UploadTask.UPLOAD_FILE)) {
            val downloadPath = findDownloadPath(packet)
            downloadFile(downloadPath)
        }
    }

    /**
     * check the validity of this transfer
     *
     * @return true if the transfer needs to be aborted, false instead
     * */
    private def checkPath(path: Path): Boolean = {
        if (Files.notExists(path)) {
            Files.createDirectories(path)
            Files.delete(path)
            Files.createFile(path)
        }
        if (!Files.isWritable(path) || !Files.isReadable(path)) {
            val errorMsg = s"($path) Can't access to the file"
            channel.sendPacket(ErrorPacket("NoSuchPermissions", errorMsg))
            fail(errorMsg)
            return true
        }
        false
    }

    private def nextPacket[P <: Packet](): P = {
        val packet = channel.nextPacket()
        packet match {
            case error: ErrorPacket =>
                throw new TaskException(error.errorMsg)
            case desired: P => desired
        }
    }


}

object DownloadTask {
    val TYPE: String = "DWN"

    def apply(transferDescription: TransferDescription): DownloadTask =
        new DownloadTask(transferDescription)


}
