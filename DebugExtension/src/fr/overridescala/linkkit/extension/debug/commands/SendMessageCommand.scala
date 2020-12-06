package fr.overridescala.linkkit.`extension`.debug.commands

import fr.overridescala.linkkit.`extension`.controller.cli.{CommandException, CommandExecutor}
import fr.overridescala.linkkit.`extension`.controller.cli.CommandExecutor
import fr.overridescala.linkkit.api.Relay

class SendMessageCommand(relay: Relay) extends CommandExecutor {

    override def execute(implicit args: Array[String]): Unit = {
        if (args.length < 1)
            throw CommandException("usage : msg <target> [message]")
        val target = args(0)
        val message = args.slice(1, args.length).mkString(" ")

        val consoleOpt = relay.getConsoleOut(target)

        if (consoleOpt.isEmpty) {
            Console.err.println(s"Could not find remote console for '$target'")
            return
        }
        consoleOpt.get.println(message)
        println(s"${relay.identifier} -> $target: $message")
    }
}
