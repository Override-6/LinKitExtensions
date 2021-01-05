package fr.`override`.linkit.`extension`.debug

import fr.`override`.linkit.`extension`.controller.ControllerExtension
import fr.`override`.linkit.`extension`.controller.cli.CommandManager
import fr.`override`.linkit.`extension`.debug._
import fr.`override`.linkit.`extension`.debug.commands.{PingCommand, SendMessageCommand, StressTestCommand}
import fr.`override`.linkit.api.Relay
import fr.`override`.linkit.api.`extension`.{RelayExtension, relayExtensionInfo}

@relayExtensionInfo(name = "DebugExtension", dependencies = Array("RelayControllerCli"))
class DebugExtension(relay: Relay) extends RelayExtension(relay) {
    override def onEnable(): Unit = {
        val completerHandler = relay.taskCompleterHandler

        completerHandler.register(PingTask.Type, (_, _) => PingTask.Completer())
        completerHandler.register(StressTestTask.Type, (init, _) => StressTestTask.Completer(init))

        val properties = relay.properties
        val commandManager = properties.getProperty(ControllerExtension.CommandManagerProp): CommandManager

        commandManager.register("ping", new PingCommand(relay))
        commandManager.register("stress", new StressTestCommand(relay))
        commandManager.register("msg", new SendMessageCommand(relay))
    }
}
