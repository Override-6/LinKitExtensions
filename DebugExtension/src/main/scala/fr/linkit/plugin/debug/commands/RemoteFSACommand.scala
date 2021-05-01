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

package fr.linkit.plugin.debug.commands

import fr.linkit.api.connection.{CentralConnection, ExternalConnection}
import fr.linkit.api.local.ApplicationContext
import fr.linkit.core.local.system.fsa.nio.NIOFileSystemAdapter
import fr.linkit.core.local.system.fsa.remote.RemoteFileSystemAdapter
import fr.linkit.plugin.controller.cli.{CommandException, CommandExecutor}

class RemoteFSACommand(context: ApplicationContext) extends CommandExecutor {


    override def execute(implicit args: Array[String]): Unit = {
        if (args.length < 3)
            throw CommandException("usage: fsa <target> <create|delete|write|read> <path> [order-args]")

        val target = args(0)
        val order  = args(1)
        val path   = args(2)

        //println(s"context.listConnections = ${context.listConnections}")
        val serverConnection = context.getConnection("TestServer1")
                .getOrElse(throw CommandException(s"TestServer1 is not connected."))
                .asInstanceOf[CentralConnection]

        val connection = serverConnection.getConnection(target).getOrElse(throw CommandException(s"$target is not connected."))

        val remoteFSA  = RemoteFileSystemAdapter.connect(classOf[NIOFileSystemAdapter], connection)

        order match {
            case "create" =>
                remoteFSA.create(path)
                println(s"Created $path on device $target")
            case "delete" =>
                remoteFSA.deleteIfExists(path)
                println(s"Removed $path on device $target")
            case "write"  =>
                val text = args.drop(2).mkString("")
                remoteFSA.getAdapter(path).write(text.getBytes)
                println(s"Written '$text' into $path on device $target")
            case "read" =>
                println(remoteFSA.getAdapter(path).getContentString)
        }
    }

}
