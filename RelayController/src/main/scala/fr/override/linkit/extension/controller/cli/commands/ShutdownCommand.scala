package fr.`override`.linkit.extension.controller.cli.commands

import fr.`override`.linkit.api.local.ApplicationContext
import fr.`override`.linkit.api.local.concurrency.Procrastinator
import fr.`override`.linkit.extension.controller.cli.CommandExecutor

class ShutdownCommand(context: ApplicationContext, procrastinator: Procrastinator) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = procrastinator.runLater {
        context.shutdown()
        System.exit(0)
    }
}
