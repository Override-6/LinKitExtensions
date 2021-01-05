package fr.`override`.linkit.`extension`.cloud.tasks

import fr.`override`.linkit.`extension`.cloud.sync.FolderSync
import fr.`override`.linkit.`extension`.cloud.tasks.SyncFoldersTask.{LOCAL_PATH_SEPARATOR, TYPE}
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.packet.channel.AsyncPacketChannel
import fr.`override`.linkit.api.packet.fundamental.TaskInitPacket
import fr.`override`.linkit.api.system.CloseReason
import fr.`override`.linkit.api.task.{Task, TaskExecutor, TaskInitInfo}

class SyncFoldersTask(relay: Relay, targetId: String, targetFolder: String, localFolder: String) extends Task[Unit](targetId) {
    setDoNotCloseChannel()

    override def initInfo: TaskInitInfo =
        TaskInitInfo.of(TYPE, targetId, targetFolder ++ LOCAL_PATH_SEPARATOR ++ localFolder)

    override def execute(): Unit = {
        channel.close(CloseReason.INTERNAL)
        val asyncChannel = relay.createChannel(channel.identifier, targetId, AsyncPacketChannel)
        val fsa = relay.configuration.fsAdapter
        new FolderSync(localFolder, targetFolder, fsa)(asyncChannel).start()
    }
}

object SyncFoldersTask {
    val TYPE = "SYNCF"
    private val LOCAL_PATH_SEPARATOR = "<local>"

    class Completer(relay: Relay, initPacket: TaskInitPacket) extends TaskExecutor {
        setDoNotCloseChannel()

        private val contentString = new String(initPacket.content)
        private val folderPathLength = contentString.indexOf(LOCAL_PATH_SEPARATOR)
        private val localFolder = contentString.substring(0, folderPathLength)
        private val remoteFolder = contentString.substring(folderPathLength + LOCAL_PATH_SEPARATOR.length, contentString.length)

        override def execute(): Unit = {
            channel.close(CloseReason.INTERNAL)
            val asyncChannel = relay.createChannel(channel.identifier, channel.connectedID, AsyncPacketChannel)
            val fsa = relay.configuration.fsAdapter
            new FolderSync(localFolder, remoteFolder, fsa)(asyncChannel).start()
        }
    }

}
