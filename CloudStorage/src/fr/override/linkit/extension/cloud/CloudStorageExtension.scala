package fr.`override`.linkit.`extension`.cloud

import java.sql.{Connection, DriverManager}

import fr.`override`.linkit.`extension`.cloud.commands.SyncDirCommand
import fr.`override`.linkit.`extension`.cloud.data.RelayStoredProperties
import fr.`override`.linkit.`extension`.cloud.sync.FolderSyncPacket
import fr.`override`.linkit.`extension`.cloud.tasks.SyncFoldersTask
import fr.`override`.linkit.`extension`.controller.ControllerExtension
import fr.`override`.linkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.RelayExtension


class CloudStorageExtension(relay: Relay) extends RelayExtension(relay) {
    private val localConnection = initLocalDb()

    override def onLoad(): Unit = {
        putFragment(new RelayStoredProperties(localConnection, relay.properties))
    }

    override def onEnable(): Unit = {
        val completerHandler = relay.taskCompleterHandler
        completerHandler.register(SyncFoldersTask.TYPE, (init, _) => new SyncFoldersTask.Completer(relay, init))

        val commandManager = getFragmentOrAbort(classOf[ControllerExtension], classOf[CommandManager])
        commandManager.register("sync", new SyncDirCommand(relay))

        val packetTranslator = relay.packetTranslator
        packetTranslator.registerFactory(FolderSyncPacket)
    }


    override def onDisable(): Unit = localConnection.close()

    def initLocalDb(): Connection = {
        Class.forName("org.sqlite.JDBC")
        val config = relay.configuration
        val fileSystem = config.fsAdapter
        val dbPath = fileSystem.getAdapter(config.extensionsFolder + "/localCloudStorage.db")
        if (dbPath.notExists)
            fileSystem.create(dbPath)
        DriverManager.getConnection(s"jdbc:sqlite:${dbPath.getAbsolutePath}")
    }

}