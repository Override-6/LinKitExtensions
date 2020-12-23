package fr.`override`.linkit.`extension`.controller

import fr.`override`.linkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkit.`extension`.controller.cli.commands.ShutdownCommand
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.{RelayExtension, relayExtensionInfo}
import ControllerExtension.CommandManagerProp


@relayExtensionInfo(name = "RelayControllerCli")
class ControllerExtension(relay: Relay) extends RelayExtension(relay) {

    private val commandManager = new CommandManager()

    override def onEnable(): Unit = {
        commandManager.register("stop", new ShutdownCommand(relay))
        commandManager.start()

        val properties = relay.properties
        properties.putProperty(CommandManagerProp, commandManager)
    }

}

object ControllerExtension {
    val CommandManagerProp: String = "command_manager"
}
