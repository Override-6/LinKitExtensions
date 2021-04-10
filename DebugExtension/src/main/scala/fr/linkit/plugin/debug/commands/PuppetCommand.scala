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

import fr.linkit.api.connection.network.cache.SharedCacheManager
import fr.linkit.core.connection.network.cache.puppet.SharedObjectsCache
import fr.linkit.core.connection.network.cache.map.SharedMap
import fr.linkit.plugin.controller.cli.{CommandException, CommandExecutor, CommandUtils}
import fr.linkit.plugin.debug.commands.PuppetCommand.Player

class PuppetCommand(cacheHandler: SharedCacheManager) extends CommandExecutor {

    private val repo    = cacheHandler.getCache(50, SharedObjectsCache)
    private val players = cacheHandler.getCache(51, SharedMap[Int, Player])
    players.link(pair => addPlayer(pair._2))

    private def addPlayer(player: Player): Unit = {
        val id = player.id
        if (!players.contains(id))
            players.put(id, player)
        repo.chipObject(id, player)
    }

    override def execute(implicit args: Array[String]): Unit = {
        val order = if (args.length == 0) "" else args(0)
        order match {
            case "create" => createPlayer(args.drop(1)) //remove first arg which is obviously 'create'
            case "update" => updatePlayer(args.drop(1))
            case "list"   => println(s"players: $players")
            case _        => throw CommandException("usage: player [create|update] [...]")
        }
    }

    private def createPlayer(args: Array[String]): Unit = {
        implicit val usage: String = "usage: player create [id=?|name=?|x=?|y=?]"

        val id     = CommandUtils.getValue("id", args).toInt
        val name   = CommandUtils.getValue("name", args)
        val x      = CommandUtils.getValue("x", args).toInt
        val y      = CommandUtils.getValue("y", args).toInt
        val player = repo.chipObject(id, Player(id, name, x, y))

        println(s"Created $player ! (identifier = $id)")
        players.put(id, player)
    }

    private def updatePlayer(args: Array[String]): Unit = {
        implicit val usage: String = "usage: player update [id=?] <name=?|x=?|y=?>"
        val id     = CommandUtils.getValue("id", args).toInt
        val player = players.getOrElse(id, throw CommandException("Player does not exists"))

        val name = CommandUtils.getValue("name", player.getName, args)
        val x    = CommandUtils.getValue("x", player.x.toString, args).toInt
        val y    = CommandUtils.getValue("y", player.y.toString, args).toInt

        println(s"Updating player $player...")
        player.setX(x)
        player.setY(y)
        player.name = name
        println(s"Player is now $player, updating chip...")
        val chip = repo.getChip[Player](id).get
        chip.sendUpdatePuppet(player)
        println(s"Chip updated !")
    }

}

object PuppetCommand {

    import fr.linkit.core.connection.network.cache.puppet.AnnotationHelper._

    @SharedObject(autoFlush = true)
    case class Player(id: Int, @Cached var name: String, @Shared var x: Long, @Shared var y: Long) extends Serializable {

        @Cached
        def getName: String = name

        @Shared
        def setX(x: Long): Unit = this.x = x

        @Shared
        def setY(y: Long): Unit = this.y = y
    }

}
