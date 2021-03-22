package fr.`override`.linkit.extension.debug

import fr.`override`.linkit.api.local.plugin.LinkitPlugin
import fr.`override`.linkit.extension.controller.ControllerExtension
import fr.`override`.linkit.extension.controller.cli.CommandManager
import fr.`override`.linkit.extension.debug.commands.{NetworkCommand, PingCommand, SendMessageCommand}
import fr.`override`.linkit.extension.debug.tests.TestCommand

class DebugExtension extends LinkitPlugin {

    override def onLoad(): Unit = {
        //putFragment(new TestRemoteFragment(relay))
    }

    override def onEnable(): Unit = {
        val commandManager = getFragmentOrAbort(classOf[ControllerExtension], classOf[CommandManager])

        commandManager.register("ping", new PingCommand(relay))
        commandManager.register("msg", new SendMessageCommand(relay))
        commandManager.register("test", new TestCommand(relay))
        commandManager.register("network", new NetworkCommand(relay.network))

        val networkHooks = relay.networkHooks

        networkHooks.entityStateChange.add(e => Log.info(s"${e.entity.identifier} got ${e.newState.name().toLowerCase}"))
        networkHooks.entityAdded.add(e => Log.info(s"New entity added : ${e.entity}"))

        ContextLogger.trace("Debug extension enabled")
    }
}
