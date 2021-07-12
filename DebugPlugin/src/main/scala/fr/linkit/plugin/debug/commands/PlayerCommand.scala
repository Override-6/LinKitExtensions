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

import fr.linkit.api.connection.cache.SharedCacheManager
import fr.linkit.api.connection.cache.repo.annotations.InvocationKind
import fr.linkit.api.connection.cache.repo.description.PuppetDescriptionBuilder
import fr.linkit.api.connection.cache.repo.description.PuppetDescriptionBuilder.MethodControl
import fr.linkit.engine.connection.cache.repo.DefaultEngineObjectCenter
import fr.linkit.plugin.controller.cli.{CommandException, CommandExecutor, CommandUtils}

import java.util
import scala.collection.mutable.ListBuffer

class PlayerCommand(cacheHandler: SharedCacheManager, supportIdentifier: String) extends CommandExecutor {

    private val repo    = cacheHandler.getCache(50, DefaultEngineObjectCenter[ListBuffer[Player]]())
    private val players = repo.findObject(0).getOrElse(repo.postObject(0, ListBuffer.empty[Player]))
    println(s"players = ${players}")

    private def addPlayer(player: Player): Unit = {
        players += player
        println(s"Added player $player in $players")
    }

    override def execute(implicit args: Array[String]): Unit = {
        val order = if (args.length == 0) "" else args(0)
        //println(s"players.toSeq = ${players}")
        //println(s"players.getChoreographer.isMethodExecutionForcedToLocal = ${players.getChoreographer.isMethodExecutionForcedToLocal}")
        order match {
            case "create" => createPlayer(args.drop(1)) //remove first arg which is obviously 'create'
            case "update" => updatePlayer(args.drop(1)) //remove first arg which is obviously 'update'
            case "list"   => println(s"players: ${repo.snapshotContent.array.map(_.puppet).mkString(", ")}")
            case "desc"   => describePlayerClass()
            case _        => throw CommandException("usage: player [create|update] [...]")
        }
    }

    private def createPlayer(args: Array[String]): Unit = {
        implicit val usage: String = "usage: player create [id=?|name=?|x=?|y=?]"

        val id     = CommandUtils.getValue("id", args).toInt
        val name   = CommandUtils.getValue("name", args)
        val x      = CommandUtils.getValue("x", args).toInt
        val y      = CommandUtils.getValue("y", args).toInt
        val player = Player(id, supportIdentifier, name, x, y)

        //println(s"Created $player ! (identifier = $id)")
        addPlayer(player)
    }

    private def describePlayerClass(): Unit = {
        //println(s"Class ${classOf[Player]}:")
        classOf[Player].getDeclaredFields.foreach(println)
    }


    private def updatePlayer(args: Array[String]): Unit = {
        implicit val usage: String = "usage: player update [id=?] <name=?|x=?|y=?>"
        val id     = CommandUtils.getValue("id", args).toInt
        val player = players.find(_.id == id).getOrElse(throw CommandException("Player not found."))

        val name = CommandUtils.getValue("name", player.name, args)
        val x    = CommandUtils.getValue("x", player.x.toString, args).toInt
        val y    = CommandUtils.getValue("y", player.y.toString, args).toInt

        //println(s"Updating player $player...")
        player.x = x
        player.y = y
        player.name = name
        //println(s"Player is now $player")
    }

    new PuppetDescriptionBuilder(repo.descriptions.getDescription[util.ArrayList[Player]]) {
        annotateAll("find") by MethodControl(InvocationKind.ONLY_LOCAL)
    }

}