package fr.overridescala.linkkit.`extension`.controller.cli.commands

import fr.overridescala.linkkit.`extension`.controller.cli.{CommandException, CommandExecutor}
import fr.overridescala.linkkit.`extension`.controller.cli.CommandExecutor
import fr.overridescala.linkkit.api.Relay
import fr.overridescala.linkkit.api.packet.fundamental.EmptyPacket
import fr.overridescala.linkkit.api.task.{Task, TaskInitInfo}

class ExecuteUnknownTaskCommand(relay: Relay) extends CommandExecutor {


    override def execute(implicit args: Array[String]): Unit = {
        if (args.length != 2)
            throw CommandException("use : exec <task_name> <target>")
        val taskName = args(0)
        val target = args(1)
        relay.scheduleTask(new UnknownTask(taskName, target))
                .queue(
                    _ => println("success !"),
                    _ => Console.err.println("error :(")
                )
    }

    private class UnknownTask(name: String, target: String) extends Task[Unit](target) {

        override def initInfo: TaskInitInfo =
            TaskInitInfo.of(name, target)

        override def execute(): Unit = {
            channel.sendPacket(EmptyPacket)
        }
    }


}
