/*
 * Copyright (c) 2021. Linkit and or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can only use it for personal uses, studies or documentation.
 * You can download this source code, and modify it ONLY FOR PERSONAL USE and you
 * ARE NOT ALLOWED to distribute your MODIFIED VERSION.
 *
 * Please contact maximebatista18@gmail.com if you need additional information or have any
 * questions.
 */

package fr.linkit.plugin.debug

import fr.linkit.api.local.plugin.LinkitPlugin
import fr.linkit.api.local.system.AppLogger
import fr.linkit.core.connection.network.cache.puppet.SharedObjectsCache
import fr.linkit.core.local.concurrency.pool.BusyWorkerPool
import fr.linkit.plugin.controller.ControllerExtension
import fr.linkit.plugin.controller.cli.CommandManager
import fr.linkit.plugin.debug.commands.{NetworkCommand, PuppetCommand}

class DebugExtension extends LinkitPlugin {

    override def onLoad(): Unit = {
        //putFragment(new TestRemoteFragment(relay))
    }

    override def onEnable(): Unit = {
        val commandManager = getFragmentOrAbort(classOf[ControllerExtension], classOf[CommandManager])

        commandManager.register("network", new NetworkCommand(getContext.listConnections.map(_.network)))

        val pool = BusyWorkerPool.currentPool.get
        pool.pauseCurrentTaskForAtLeast(5000)

        val testServerConnection = getContext.getConnection("TestServer1").get
        val globalCache = testServerConnection.network.globalCache
        commandManager.register("player", new PuppetCommand(globalCache))

        AppLogger.trace("Debug extension enabled.")
    }
}
