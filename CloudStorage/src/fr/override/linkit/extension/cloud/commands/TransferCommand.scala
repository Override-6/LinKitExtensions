package fr.`override`.linkit.`extension`.cloud.commands

import fr.`override`.linkit.`extension`.cloud.tasks
import fr.`override`.linkit.`extension`.cloud.tasks.{DownloadTask, UploadTask}
import fr.`override`.linkit.`extension`.cloud.transfer.TransferDescriptionBuilder
import fr.`override`.linkit.`extension`.controller.cli.{CommandException, CommandExecutor, CommandUtils}
import fr.`override`.linkit.`extension`.cloud.tasks.UploadTask
import fr.`override`.linkit.`extension`.controller.cli.CommandUtils
import fr.`override`.linkit.`extension`.controller.cli.CommandUtils.argAfter
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.task.Task

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
        val task: Task[Unit] = if (isDownload) tasks.DownloadTask(desc) else UploadTask(desc)
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
