package fr.`override`.linkit.extension.controller.cli.commands

import fr.`override`.linkit.skull.Relay
import fr.`override`.linkit.skull.internal.system.CloseReason
import fr.`override`.linkit.extension.controller.cli.CommandExecutor

class ShutdownCommand(relay: Relay) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = relay.runLater {
        relay.close(CloseReason.INTERNAL)
        System.exit(0)
    }
}
