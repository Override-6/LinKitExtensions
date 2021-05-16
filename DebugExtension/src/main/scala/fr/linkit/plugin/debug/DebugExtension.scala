/*
 *  Copyright (c) 2021. Linkit and or its affiliates. All rights reserved.
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  This code is free software; you can only use it for personal uses, studies or documentation.
 *  You can download this source code, and modify it ONLY FOR PERSONAL USE and you
 *  ARE NOT ALLOWED to distribute your MODIFIED VERSION.
 *
 *  Please contact maximebatista18@gmail.com if you need additional information or have any
 *  questions.
 */

package fr.linkit.plugin.debug

import fr.linkit.api.local.concurrency.WorkerPools
import fr.linkit.api.local.plugin.LinkitPlugin
import fr.linkit.api.local.resource.external.{ResourceFile, ResourceFolder}
import fr.linkit.api.local.system.AppLogger
import fr.linkit.engine.local.concurrency.pool.{BusyWorkerPool, SimpleWorkerController}
import fr.linkit.engine.local.resource.external.{LocalResourceFile, LocalResourceFolder}
import fr.linkit.plugin.controller.ControllerExtension
import fr.linkit.plugin.controller.cli.CommandManager
import fr.linkit.plugin.debug.commands.{NetworkCommand, PlayerCommand, RemoteFSACommand}

class DebugExtension extends LinkitPlugin {

    override def onLoad(): Unit = {
        //putFragment(new TestRemoteFragment(relay))
    }

    override def onEnable(): Unit = {
        val commandManager = getFragmentOrAbort(classOf[ControllerExtension], classOf[CommandManager])


        val pool       = WorkerPools.currentPool.get
        val controller = new SimpleWorkerController()
        controller.pauseCurrentTask(2000)

        val testServerConnection = getContext.getConnection("TestServer1").get
        val globalCache          = testServerConnection.network.cache
        val resources            = getContext.getAppResources

        val file = resources.getOrOpen[LocalResourceFolder]("Test.exe")
        val folder = resources.getOrOpen[LocalResourceFolder]("MyFolder")


        commandManager.register("player", new PlayerCommand(globalCache, testServerConnection.supportIdentifier))
        commandManager.register("network", new NetworkCommand(getContext.listConnections.map(_.network)))
        commandManager.register("fsa", new RemoteFSACommand(getContext))

        AppLogger.trace("Debug extension enabled.")
    }
}
