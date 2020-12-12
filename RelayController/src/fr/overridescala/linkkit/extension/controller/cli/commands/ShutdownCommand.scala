package fr.overridescala.linkkit.`extension`.controller.cli.commands

import fr.overridescala.linkkit.`extension`.controller.cli.CommandExecutor
import fr.overridescala.linkkit.api.Relay
import fr.overridescala.linkkit.api.system.Reason

class ShutdownCommand(relay: Relay) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = relay.close(Reason.INTERNAL)
}
