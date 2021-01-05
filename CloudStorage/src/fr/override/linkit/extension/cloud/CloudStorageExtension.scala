package fr.`override`.linkit.`extension`.cloud

import fr.`override`.linkit.`extension`.cloud.commands.SyncDirCommand
import fr.`override`.linkit.`extension`.cloud.sync.FolderSyncPacket
import fr.`override`.linkit.`extension`.cloud.tasks.SyncFoldersTask
import fr.`override`.linkit.`extension`.controller.ControllerExtension
import fr.`override`.linkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.{RelayExtension, relayExtensionInfo}

@relayExtensionInfo(name = "CloudStorageExtension", dependencies = Array("RelayControllerCli"))
class CloudStorageExtension(relay: Relay) extends RelayExtension(relay) {

    override def onEnable(): Unit = {
        val completerHandler = relay.taskCompleterHandler
        completerHandler.register(SyncFoldersTask.TYPE, (init, coords) => new SyncFoldersTask.Completer(relay, init))

        val properties = relay.properties
        val commandManager = properties.getProperty(ControllerExtension.CommandManagerProp): CommandManager
        commandManager.register("sync", new SyncDirCommand(relay))

        val packetTranslator = relay.packetTranslator
        packetTranslator.registerFactory(FolderSyncPacket)
    }

}