package fr.`override`.linkit.extension.controller

import fr.`override`.linkit.api.local.plugin.LinkitPlugin
import fr.`override`.linkit.extension.controller.cli.CommandManager
import fr.`override`.linkit.extension.controller.cli.commands.ShutdownCommand


class ControllerExtension extends LinkitPlugin {

    private val commandManager = new CommandManager()

    override def onLoad(): Unit = {
        putFragment(commandManager)
    }

    override def onEnable(): Unit = {
        commandManager.register("stop", new ShutdownCommand(getContext, getProcrastinator))
    }

}