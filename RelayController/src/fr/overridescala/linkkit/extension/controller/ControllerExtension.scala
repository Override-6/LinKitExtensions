package fr.overridescala.linkkit.`extension`.controller

import fr.overridescala.linkkit.`extension`.controller.auto.AutomationManager
import fr.overridescala.linkkit.`extension`.controller.cli.CommandManager
import ControllerExtension.{AutomationManagerProp, CommandManagerProp}
import fr.overridescala.linkkit.`extension`.controller.cli.commands.{ExecuteUnknownTaskCommand, ShutdownCommand}
import fr.overridescala.linkkit.`extension`.controller.cli.commands._
import fr.overridescala.linkkit.api.Relay
import fr.overridescala.linkkit.api.`extension`.{RelayExtension, relayExtensionInfo}


@relayExtensionInfo(name = "RelayControllerCli")
class ControllerExtension(relay: Relay) extends RelayExtension(relay) {

    private val automationManager = new AutomationManager()
    private val commandManager = new CommandManager()

    override def onEnable(): Unit = {

        commandManager.register("exec", new ExecuteUnknownTaskCommand(relay))
        commandManager.register("stop", new ShutdownCommand())
        commandManager.start()
        automationManager.start()

        val properties = relay.properties
        properties.putProperty(AutomationManagerProp, automationManager)
        properties.putProperty(CommandManagerProp, commandManager)
    }

}

object ControllerExtension {
    val AutomationManagerProp: String = "automation_manager"
    val CommandManagerProp: String = "command_manager"
}
