package fr.`override`.linkit.`extension`.controller.cli.commands

import fr.`override`.linkit.`extension`.controller.cli.CommandExecutor
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.system.Reason

class ShutdownCommand(relay: Relay) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = relay.close(Reason.INTERNAL)
}
