package fr.`override`.linkit.`extension`.debug.tests

import fr.`override`.linkit.`extension`.controller.cli.CommandExecutor
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.utils.cache.SharedCollection

class TestCommand(relay: Relay) extends CommandExecutor {

    private val sharedCollection = SharedCollection.open[String](145)(relay.traffic)
            .addListener((a, b, c) => println(a, b, c))
    private val Set = 1
    private val Clear = 2
    private val Remove = 3
    private val Add = 4
    private val Flush = 5

    override def execute(implicit args: Array[String]): Unit = {
        /*val target = args(0)
        val fragName = args(1)
        val targetEntity = relay.network.getEntity(target).get
        val remoteFragment = targetEntity.getRemoteFragmentController(fragName).get

        remoteFragment.send(EmptyPacket)
        remoteFragment.addOnPacketReceived(println)*/

        val modKind = args(0).toInt
        lazy val index = args(1).toInt
        lazy val item = args(2)

        modKind match {
            case Set => sharedCollection.set(index, item)
            case Clear => sharedCollection.clear()
            case Remove => sharedCollection.remove(index)
            case Add => sharedCollection.add(index, item)
            case Flush => sharedCollection.flush()
        }
        println(s"Applied modification '$modKind'")
    }

}
