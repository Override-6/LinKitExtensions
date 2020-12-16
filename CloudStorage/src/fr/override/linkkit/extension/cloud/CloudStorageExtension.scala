package fr.`override`.linkkit.`extension`.cloud

import fr.`override`.linkkit.`extension`.cloud.commands.{SyncDirCommand, TransferCommand}
import fr.`override`.linkkit.`extension`.cloud.sync.FolderSyncPacket
import fr.`override`.linkkit.`extension`.cloud.tasks.{DownloadTask, SyncFoldersTask, UploadTask}
import fr.`override`.linkkit.`extension`.controller.ControllerExtension
import fr.`override`.linkkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkkit.`extension`.cloud.commands.TransferCommand
import fr.`override`.linkkit.`extension`.cloud.tasks.SyncFoldersTask
import fr.`override`.linkkit.api.Relay
import fr.`override`.linkkit.api.`extension`.{RelayExtension, relayExtensionInfo}
import fr.`override`.linkkit.api.utils.Utils

@relayExtensionInfo(name = "CloudStorageExtension", dependencies = Array("RelayControllerCli"))
class CloudStorageExtension(relay: Relay) extends RelayExtension(relay) {

    override def onEnable(): Unit = {
        val completerHandler = relay.taskCompleterHandler
        completerHandler.putCompleter(UploadTask.TYPE, init => DownloadTask(Utils.deserialize(init.content)))
        completerHandler.putCompleter(DownloadTask.TYPE, init => UploadTask(Utils.deserialize(init.content)))
        completerHandler.putCompleter(SyncFoldersTask.TYPE, init => new SyncFoldersTask.Completer(relay, init))

        val properties = relay.properties
        val commandManager = properties.getProperty(ControllerExtension.CommandManagerProp): CommandManager
        commandManager.register("sync", new SyncDirCommand(relay))
        commandManager.register("download", TransferCommand.download(relay))
        commandManager.register("upload", TransferCommand.upload(relay))

        val packetManager = relay.packetManager
        packetManager.register(FolderSyncPacket)
    }

}