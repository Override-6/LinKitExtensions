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

import fr.linkit.api.local.plugin.LinkitPlugin
import fr.linkit.api.local.resource.AutomaticBehaviorOption
import fr.linkit.api.local.resource.representation.{ResourceFile, ResourceFolder}
import fr.linkit.api.local.system.AppLogger
import fr.linkit.core.local.concurrency.pool.{BusyWorkerPool, DedicatedWorkerController}
import fr.linkit.plugin.controller.ControllerExtension
import fr.linkit.plugin.controller.cli.CommandManager
import fr.linkit.plugin.debug.commands.{NetworkCommand, PuppetCommand}

import scala.util.Try

class DebugExtension extends LinkitPlugin {

    override def onLoad(): Unit = {
        //putFragment(new TestRemoteFragment(relay))
    }

    override def onEnable(): Unit = {
        val commandManager = getFragmentOrAbort(classOf[ControllerExtension], classOf[CommandManager])

        commandManager.register("network", new NetworkCommand(getContext.listConnections.map(_.network)))

        val pool       = BusyWorkerPool.currentPool.get
        val controller = new DedicatedWorkerController(pool)
        controller.waitTaskWhile {
            println(s"getContext.listConnections = ${getContext.listConnections}")
            getContext.getConnection("TestServer1").isEmpty
        }

        val testServerConnection = getContext.getConnection("TestServer1").get
        val globalCache          = testServerConnection.network.globalCache
        val resources            = getContext.getAppResources

        println(s"resources = ${resources}")
        val file = Try(resources.openResourceFile("Test.exe", true))
                .getOrElse(resources.get[ResourceFile]("Test.exe"))

        val folder = Try(resources.openResourceFolder("MyFolder", false, AutomaticBehaviorOption.values(): _*))
                .getOrElse(resources.get[ResourceFolder]("MyFolder"))

        println(s"file = ${file}")
        println(s"folder = ${folder}")

        commandManager.register("player", new PuppetCommand(globalCache, testServerConnection.supportIdentifier))

        AppLogger.trace("Debug extension enabled.")
    }
}
