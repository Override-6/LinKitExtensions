package fr.`override`.linkit.`extension`.cloud

import fr.`override`.linkit.`extension`.cloud.commands.{SyncDirCommand, TransferCommand}
import fr.`override`.linkit.`extension`.cloud.sync.FolderSyncPacket
import fr.`override`.linkit.`extension`.cloud.tasks.{DownloadTask, SyncFoldersTask, UploadTask}
import fr.`override`.linkit.`extension`.controller.ControllerExtension
import fr.`override`.linkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkit.`extension`.cloud.commands.SyncDirCommand
import fr.`override`.linkit.`extension`.cloud.tasks.UploadTask
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.{RelayExtension, relayExtensionInfo}
import fr.`override`.linkit.api.utils.Utils

@relayExtensionInfo(name = "CloudStorageExtension", dependencies = Array("RelayControllerCli"))
class CloudStorageExtension(relay: Relay) extends RelayExtension(relay) {

    override def onEnable(): Unit = {
        val completerHandler = relay.taskCompleterHandler
        completerHandler.register(UploadTask.TYPE, (init, coords) => DownloadTask(Utils.deserialize(init.content)))
        completerHandler.register(DownloadTask.TYPE, (init, coords) => UploadTask(Utils.deserialize(init.content)))
        completerHandler.register(SyncFoldersTask.TYPE, (init, coords) => new SyncFoldersTask.Completer(relay, init))

        val properties = relay.properties
        val commandManager = properties.getProperty(ControllerExtension.CommandManagerProp): CommandManager
        commandManager.register("sync", new SyncDirCommand(relay))
        commandManager.register("download", TransferCommand.download(relay))
        commandManager.register("upload", TransferCommand.upload(relay))

        val packetManager = relay.packetManager
        packetManager.register(FolderSyncPacket)
    }

}