package fr.overridescala.linkkit.`extension`.cloud

import fr.overridescala.linkkit.`extension`.cloud.commands.{SyncDirCommand, TransferCommand}
import fr.overridescala.linkkit.`extension`.cloud.sync.FolderSyncPacket
import fr.overridescala.linkkit.`extension`.cloud.tasks.{DownloadTask, SyncFoldersTask, UploadTask}
import fr.overridescala.linkkit.`extension`.controller.ControllerExtension
import fr.overridescala.linkkit.`extension`.controller.cli.CommandManager
import fr.overridescala.linkkit.`extension`.cloud.commands.TransferCommand
import fr.overridescala.linkkit.`extension`.cloud.tasks.SyncFoldersTask
import fr.overridescala.linkkit.api.Relay
import fr.overridescala.linkkit.api.`extension`.{RelayExtension, relayExtensionInfo}
import fr.overridescala.linkkit.api.utils.Utils

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