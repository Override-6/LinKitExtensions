package fr.`override`.linkit.extension.controller

import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.local.plugin.Plugin
import fr.`override`.linkit.extension.controller.cli.CommandManager
import fr.`override`.linkit.extension.controller.cli.commands.ShutdownCommand


class ControllerExtension(relay: Relay) extends Plugin(relay) {

    private val commandManager = new CommandManager()

    override def onLoad(): Unit = {
        putFragment(commandManager)
    }

    override def onEnable(): Unit = {
        commandManager.register("stop", new ShutdownCommand(relay))
    }

}