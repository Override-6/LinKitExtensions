package fr.overridescala.linkkit.`extension`.controller.cli.commands

import fr.overridescala.linkkit.`extension`.controller.cli.CommandExecutor

class ShutdownCommand extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = System.exit(0)
}
