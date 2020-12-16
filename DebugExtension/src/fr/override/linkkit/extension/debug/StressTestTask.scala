package fr.`override`.linkkit.`extension`.debug

import fr.`override`.linkkit.api.packet.fundamental.{DataPacket, TaskInitPacket}
import fr.`override`.linkkit.api.task.{Task, TaskExecutor, TaskInitInfo}
import StressTestTask.{Type, download, upload}
import fr.`override`.linkkit.api.packet.channel.PacketChannel

/**
 * This is a Test task, will not be documented.
 *
 * wait
 * */
class StressTestTask(private val totalDataLength: Long,
                     private val isDownload: Boolean) extends Task[Unit]("server") {

    override val initInfo: TaskInitInfo = {
        val downloadBit: Byte = if (isDownload) 1 else 0
        TaskInitInfo.of(Type, "server", Array(downloadBit) ++ s"$totalDataLength".getBytes())
    }

    override def execute(): Unit = {
        if (isDownload)
            download(channel, totalDataLength)
        else upload(channel, totalDataLength)
        success()
    }


}

object StressTestTask {

    private val CONTINUE = "PCKT"
    private val END = "END"
    val Type = "STRSS"


    case class Completer(initPacket: TaskInitPacket) extends TaskExecutor {

        private val content = initPacket.content
        private val isDownload: Boolean = content(0) != 1
        private val totalDataLength: Long = new String(content.slice(1, content.length)).toLong

        override def execute(): Unit = {
            if (isDownload)
                download(channel, totalDataLength)
            else upload(channel, totalDataLength)
        }
    }

    private def upload(implicit channel: PacketChannel, totalDataLength: Long): Unit = {
        println("UPLOAD")
        var totalSent: Float = 0
        val capacity = Int.MaxValue / 200
        println(s"capacity = ${capacity}")
        var bytes = new Array[Byte](capacity)
        java.util.Arrays.fill(bytes, 45.asInstanceOf[Byte])
        var maxBPS = 0F
        while (totalSent < totalDataLength) {
            if (totalDataLength - totalSent < capacity)
                bytes = new Array[Byte]((totalDataLength - totalSent).toInt)

            val t0 = System.currentTimeMillis()

            channel.sendPacket(DataPacket(CONTINUE, bytes))

            val t1 = System.currentTimeMillis()
            val time: Float = t1 - t0

            totalSent += capacity

            val percentage = totalSent / totalDataLength * 100
            var bps = capacity / (time / 1000)
            if (bps == Float.PositiveInfinity)
                bps = 0
            maxBPS = Math.max(bps, maxBPS)
            print(s"\rjust sent ${capacity} in $time ms ${bps} bytes/s (${totalSent} / $totalDataLength $percentage%) (max b/s = ($maxBPS)")
        }
        channel.sendPacket(DataPacket(END))
        println()
    }

    private def download(channel: PacketChannel.Sync, totalDataLength: Long): Unit = {
        println("DOWNLOAD")
        var packet = channel.nextPacketAsP(): DataPacket
        var totalReceived: Float = 0
        var maxBPS = 0F
        while (!packet.header.equals(END)) {
            val t0 = System.currentTimeMillis()
            packet = channel.nextPacketAsP(): DataPacket
            val dataLength = packet.content.length
            val t1 = System.currentTimeMillis()
            val time: Float = t1 - t0

            totalReceived += dataLength

            val percentage = totalReceived / totalDataLength * 100
            var bps = dataLength / (time / 1000)
            if (bps == Float.PositiveInfinity)
                bps = 0
            maxBPS = Math.max(bps, maxBPS)
            print(s"\rjust received ${dataLength} in $time ms ${bps}  bytes/s (${dataLength} / $totalDataLength $percentage%) (max b/s = ($maxBPS)")
        }
    }

    def apply(totalDataLength: Int, isDownload: Boolean): StressTestTask =
        new StressTestTask(totalDataLength, isDownload)


}
