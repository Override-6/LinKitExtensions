package fr.`override`.linkkit.`extension`.controller.cli.commands

import fr.`override`.linkkit.`extension`.controller.cli.CommandExecutor
import fr.`override`.linkkit.api.Relay
import fr.`override`.linkkit.api.system.Reason

class ShutdownCommand(relay: Relay) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = relay.close(Reason.INTERNAL)
}
