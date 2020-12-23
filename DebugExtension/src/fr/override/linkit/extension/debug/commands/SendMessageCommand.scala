package fr.`override`.linkit.`extension`.debug.commands

import fr.`override`.linkit.`extension`.controller.cli.{CommandException, CommandExecutor}
import fr.`override`.linkit.`extension`.controller.cli.CommandException
import fr.`override`.linkit.api.Relay

class SendMessageCommand(relay: Relay) extends CommandExecutor {

    override def execute(implicit args: Array[String]): Unit = {
        val isErr = args.contains("-R")

        val targetIndex = if(isErr) 1 else 0
        if (args.length < targetIndex + 1)
            throw CommandException("usage : msg [-R] <target> [message]")

        val target = args(targetIndex)
        val message = args.slice(targetIndex + 1, args.length).mkString(" ")

        val consoleOpt = if (isErr) relay.getConsoleErr(target) else relay.getConsoleOut(target)

        if (consoleOpt.isEmpty) {
            Console.err.println(s"Could not find remote console for '$target'")
            return
        }
        consoleOpt.get.println(message)
        println(s"${relay.identifier} -> $target: $message")
    }
}
