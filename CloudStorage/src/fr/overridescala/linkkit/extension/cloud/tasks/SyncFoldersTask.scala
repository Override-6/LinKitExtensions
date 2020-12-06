package fr.overridescala.linkkit.`extension`.cloud.tasks

import SyncFoldersTask.{LOCAL_PATH_SEPARATOR, TYPE}
import fr.overridescala.linkkit.`extension`.cloud.sync.FolderSync
import fr.overridescala.linkkit.api.Relay
import fr.overridescala.linkkit.api.packet.fundamental.TaskInitPacket
import fr.overridescala.linkkit.api.system.Reason
import fr.overridescala.linkkit.api.task.{Task, TaskExecutor, TaskInitInfo}

class SyncFoldersTask(relay: Relay, targetId: String, targetFolder: String, localFolder: String) extends Task[Unit](targetId) {
    setDoNotCloseChannel()

    override def initInfo: TaskInitInfo =
        TaskInitInfo.of(TYPE, targetId, targetFolder ++ LOCAL_PATH_SEPARATOR ++ localFolder)

    override def execute(): Unit = {
        channel.close(Reason.INTERNAL)
        val asyncChannel = relay.createAsyncChannel(targetId, channel.channelID)
        new FolderSync(localFolder, targetFolder)(asyncChannel).start()
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
            channel.close(Reason.INTERNAL)
            val asyncChannel = relay.createAsyncChannel(channel.connectedID, channel.channelID)
            new FolderSync(localFolder, remoteFolder)(asyncChannel).start()
        }
    }

}
