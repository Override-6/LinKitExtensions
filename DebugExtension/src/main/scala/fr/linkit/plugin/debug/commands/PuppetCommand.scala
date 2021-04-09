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

import fr.linkit.core.connection.network.cache.`object`.{Cached, Shared, SharedObject, SharedObjectsCache}
import fr.linkit.plugin.controller.cli.{CommandException, CommandExecutor, CommandUtils}
import fr.linkit.plugin.debug.commands.PuppetCommand.Player

import scala.collection.mutable

class PuppetCommand(repo: SharedObjectsCache) extends CommandExecutor {

    private val players = new mutable.HashMap[Int, Player]()

    override def execute(implicit args: Array[String]): Unit = {
        val order = args(0)
        order match {
            case "create" => createPlayer(args.drop(1)) //remove first arg which is obviously 'create'
            case "update" => updatePlayer(args.drop(1))
        }
    }

    private def createPlayer(args: Array[String]): Unit = {
        if (args.length != 4)
            throw CommandException("usage: player create [id=?|name=?|x=?|y=?]")

        val name   = CommandUtils.getValue("name", "James", args)
        val id     = CommandUtils.getValue("id", "-1", args).toInt
        val x      = CommandUtils.getValue("x", "50", args).toInt
        val y      = CommandUtils.getValue("y", "20", args).toInt
        val player = repo.chipObject(id, Player(name, x, y))
        println(s"Created $player ! (identifier = $id)")
        players.put(id, player)
    }

    private def updatePlayer(args: Array[String]): Unit = {
        if (args.length > 4 || args.length == 0)
            throw CommandException("usage: player update [id=?] <|name=?|x=?|y=?>")

    }

}

object PuppetCommand {

    @SharedObject(autoFlush = true)
    case class Player(@Cached name: String, var x: Int, var y: Int) extends Serializable {

        @Cached
        def getName: String = name

        @Shared
        def setX(x: Int): Unit = this.x = x

        @Shared
        def setY(y: Int): Unit = this.y = y
    }

}
