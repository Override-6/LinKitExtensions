package fr.`override`.linkit.`extension`.cloud.sync

import java.io.File
import java.nio.file.StandardWatchEventKinds._
import java.nio.file._

import com.sun.nio.file.{ExtendedWatchEventModifier, SensitivityWatchEventModifier}
import fr.`override`.linkit.api.packet.channel.PacketChannel
import fr.`override`.linkit.api.packet.{Packet, PacketCoordinates}
import fr.`override`.linkit.api.system.fsa.{FileAdapter, FileSystemAdapter}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class FolderSync(localPath: String,
                 targetPath: String, fsa: FileSystemAdapter)(implicit channel: PacketChannel.Async) {

    private val ignoredPaths = ListBuffer.empty[FileAdapter]
    private val watchService = FileSystems.getDefault.newWatchService()
    private val listener: FolderListener = new FolderListener()(channel, fsa)

    channel onPacketInjected handlePacket

    def start(): Unit = {
        new Thread(() => {
            startWatchService()
        }).start()
    }

    private def startWatchService(): Unit = {
        val events = Array(ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY): Array[WatchEvent.Kind[_]]
        Paths.get(localPath).register(watchService, events, ExtendedWatchEventModifier.FILE_TREE, SensitivityWatchEventModifier.HIGH)

        var key = watchService.take()
        while (key != null) {
            dispatchEvents(key)
            key.reset()
            key = watchService.take()
        }
    }

    private def filterEvents(events: ListBuffer[WatchEvent[Path]]): Unit = {
        val noFiltered = events.filter(_.kind() == ENTRY_DELETE)
        val toFilter = events.filterNot(_.kind() == ENTRY_DELETE)
        val pathEvents = mutable.Map.empty[Path, WatchEvent[Path]]

        for (event <- toFilter) {
            val path = event.context()
            if (!pathEvents.contains(path))
                pathEvents.put(path, event)
        }
        events.clear()
        events ++= pathEvents.values ++= noFiltered
    }

    private def replaceCreateEvents(events: ListBuffer[WatchEvent[Path]]): Unit = {
        val paths = events.map(_.context())
        events.filterInPlace(event => {
            val count = paths.count(_ == event.context())
            count == 1 || event.kind() == ENTRY_CREATE
        })
    }

    private def dispatchEvents(key: WatchKey): Unit = {
        val events = key
                .pollEvents()
                .toArray(Array[WatchEvent[Path]]())
                .to(ListBuffer)

        val dir = key.watchable().asInstanceOf[Path]
        dispatchRenameEvents(dir, events)

        filterEvents(events)
        replaceCreateEvents(events)

        println(s"events = ${events}")

        for (event <- events)
            dispatchEvent(event)

        def dispatchEvent(event: WatchEvent[Path]): Unit = {
            val context = event.context()
            val affected = fsa.getAdapter(dir.resolve(context).toString)

            if (ignoredPaths.clone().contains(affected)) {
                ignoredPaths -= affected
                println(s"IGNORED EVENT ${event.kind()} FOR $affected")
                return
            }
            //println(s"DETECTED EVENT ${event.kind()} FOR $affected")

            println(affected + " -" + event.kind())


            event.kind() match {
                case ENTRY_DELETE => listener.onDelete(affected)
                case ENTRY_MODIFY | ENTRY_CREATE => listener.onModify(affected)
            }
        }
    }

    def dispatchRenameEvents(dir: Path, events: ListBuffer[WatchEvent[Path]]): Unit = {
        if (events.length < 2)
            return
        var lastEvent: WatchEvent[Path] = events.head

        events.foreach(compareEvent)

        def compareEvent(event: WatchEvent[Path]): Unit = {
            val kind = event.kind()
            val lastKind = lastEvent.kind()

            if (lastKind == ENTRY_DELETE && kind == ENTRY_CREATE) {
                val newName = event.context().toString
                val affected = fsa.getAdapter(dir.resolve(lastEvent.context()).toString)

                listener.onRename(affected, newName)
                events -= event -= lastEvent
                return
            }
            lastEvent = event
        }

    }


    private def deletePath(path: FileAdapter): Unit = {
        if (path.notExists)
            return
        deleteRecursively(path)

        def deleteRecursively(path: FileAdapter): Unit = {
            if (path.isDirectory) {
                fsa.list(path).foreach(deleteRecursively)
            }
            try
                fsa.delete(path)
            catch {
                case e: FileSystemException =>
                    e.printStackTrace()
            }
        }
    }

    private def handlePacket(packet: Packet, coords: PacketCoordinates): Unit = {
        val syncPacket = packet.asInstanceOf[FolderSyncPacket]
        val order = syncPacket.order
        val affected = toLocal(syncPacket.affectedPath)

        ignoredPaths += affected
        order match {
            case "upload" => handleFileDownload(syncPacket)
            case "rename" => handleRenameOrder(syncPacket)
            case "delete" => deletePath(affected)
            case "mkdirs" => fsa.createDirectories(affected)
        }
    }

    private def handleRenameOrder(syncPacket: FolderSyncPacket): Unit = {
        val newName = new String(syncPacket.content)
        val path = toLocal(syncPacket.affectedPath)

        val renamed = path.resolveSibling(newName)
        if (path exists)
            fsa.move(path, renamed)
    }

    private def handleFileDownload(syncPacket: FolderSyncPacket): Unit = {
        val remotePath = syncPacket.affectedPath
        val path = toLocal(syncPacket.affectedPath)
        println("downloading " + remotePath)


        if (path notExists) {
            fsa.createDirectories(path.getParent)
            if (path.toString.contains('.') && path.notExists)
                fsa.create(path)
        }
        if (path.isWritable)
            path.write(syncPacket.content)
    }

    private def toLocal(nonLocalPath: String): FileAdapter =
        fsa.getAdapter(nonLocalPath).getParent(targetNameCount)

    private val targetNameCount = targetPath.count(_ == File.separatorChar)

}