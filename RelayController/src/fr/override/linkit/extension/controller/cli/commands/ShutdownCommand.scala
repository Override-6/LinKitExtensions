package fr.`override`.linkit.`extension`.controller.cli.commands

import fr.`override`.linkit.`extension`.controller.cli.CommandExecutor
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.system.CloseReason

class ShutdownCommand(relay: Relay) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = relay.close(CloseReason.INTERNAL)
}
