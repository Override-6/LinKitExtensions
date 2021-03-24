package fr.`override`.linkit.plugin.debug

import fr.`override`.linkit.api.local.plugin.LinkitPlugin
import fr.`override`.linkit.core.local.system.ContextLogger
import fr.`override`.linkit.plugin.controller.ControllerExtension
import fr.`override`.linkit.plugin.controller.cli.CommandManager
import fr.`override`.linkit.plugin.debug.commands.NetworkCommand

class DebugExtension extends LinkitPlugin {

    override def onLoad(): Unit = {
        //putFragment(new TestRemoteFragment(relay))
    }

    override def onEnable(): Unit = {
        val commandManager = getFragmentOrAbort(classOf[ControllerExtension], classOf[CommandManager])

        commandManager.register("network", new NetworkCommand(getContext.listConnections.map(_.network)))

        ContextLogger.trace("Debug extension enabled.")
    }
}
