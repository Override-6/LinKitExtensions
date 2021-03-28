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

package fr.linkit.plugin.controller.cli.commands

import fr.linkit.api.local.ApplicationContext
import fr.linkit.plugin.controller.cli.CommandExecutor

class ShutdownCommand(context: ApplicationContext) extends CommandExecutor {
    override def execute(implicit args: Array[String]): Unit = context.runLater {
        context.shutdown()
        System.exit(0)
    }
}
