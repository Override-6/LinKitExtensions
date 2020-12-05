package fr.overridescala.vps.ftp.`extension`.cloud.commands

import fr.overridescala.vps.ftp.`extension`.cloud.tasks.{DownloadTask, UploadTask}
import fr.overridescala.vps.ftp.`extension`.cloud.transfer.TransferDescriptionBuilder
import fr.overridescala.vps.ftp.`extension`.controller.cli.CommandUtils.argAfter
import fr.overridescala.vps.ftp.`extension`.controller.cli.{CommandException, CommandExecutor, CommandUtils}
import fr.overridescala.vps.ftp.api.Relay
import fr.overridescala.vps.ftp.api.task.Task

/**
 * syntax : <p>
 * upload | download -s "sourceP path" -t "target identifier" -d "destination path"
 * */
class TransferCommand private(private val relay: Relay,
                              private val isDownload: Boolean) extends CommandExecutor {


    override def execute(implicit args: Array[String]): Unit = {
        checkArgs(args)
        val target = argAfter("-t")
        val sourcePath = argAfter("-s")
        val dest = argAfter("-d")

        val desc = new TransferDescriptionBuilder {
            source = sourcePath
            targetID = target
            destination = dest
        }
        val task: Task[Unit] = if (isDownload) DownloadTask(desc) else UploadTask(desc)
        relay.scheduleTask(task)
                .complete()
    }

    def checkArgs(implicit args: Array[String]): Unit = {
        if (args.length != 6)
            throw CommandException("use: upload|download -t <target> -s <source_path> -d <target_destination>")
        CommandUtils.checkArgsContains("-t", "-s", "-d")
    }

}

object TransferCommand {
    def download(relay: Relay): TransferCommand = {
        new TransferCommand(relay, true)
    }

    def upload(relay: Relay): TransferCommand =
        new TransferCommand(relay, false)
}
