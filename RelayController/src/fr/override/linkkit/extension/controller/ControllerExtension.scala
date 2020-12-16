package fr.`override`.linkkit.`extension`.controller

import fr.`override`.linkkit.`extension`.controller.ControllerExtension.CommandManagerProp
import fr.`override`.linkkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkkit.`extension`.controller.cli.commands.ShutdownCommand
import fr.`override`.linkkit.api.Relay
import fr.`override`.linkkit.api.`extension`.{RelayExtension, relayExtensionInfo}


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
