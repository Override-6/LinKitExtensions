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

package fr.linkit.plugin.controller

import fr.linkit.api.local.plugin.LinkitPlugin
import fr.linkit.plugin.controller.cli.CommandManager
import fr.linkit.plugin.controller.cli.commands.ShutdownCommand

class ControllerExtension extends LinkitPlugin {

    private var commandManager: CommandManager = _

    override def onLoad(): Unit = {
        commandManager = new CommandManager(getContext)
        putFragment(commandManager)
    }

    override def onEnable(): Unit = {
        commandManager.register("stop", new ShutdownCommand(getContext))
        commandManager.start()
    }

}