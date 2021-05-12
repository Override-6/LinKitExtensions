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

import fr.linkit.api.connection.network.{Engine, Network}
import fr.linkit.plugin.controller.cli.CommandExecutor

import java.time.{Duration, LocalDateTime}

class NetworkCommand(networks: => Iterable[Network]) extends CommandExecutor {

    override def execute(implicit args: Array[String]): Unit = {
        //println(s"networks = ${networks}")
        networks.foreach(genDescription)
    }

    private def genDescription(network: Network): Unit = {
        network.connection.runLater {
            val engines      = network.listEngines
            val enginesName = engines.map(_.identifier).mkString(", ")
            val count       = engines.size
            val upDate      = network.startUpDate
            val self        = network.connectionEngine
            val duration    = getDurationAsString(upDate.toLocalDateTime)

            println(s"${network.serverIdentifier}:")
            println(s"There are $count engines over this network.")
            println(s"Started at $upDate (Since: $duration)")
            println(s"Self engine : $self")
            println(s"\tStatus : ${self.getConnectionState}")
            println(s"All currently connected engines : $enginesName")
            println("--------------------------------------------------")
            println("For all engines : ")
            engines.foreach(genDescription)
        }
    }

    private def genDescription(engine: Engine): Unit = {
        val name               = engine.identifier
        val connectionDate     = engine.connectionDate
        val versions           = engine.versions
        val connectionDuration = getDurationAsString(connectionDate.toLocalDateTime)

        println(s"-$name : ")
        println(s"\tStatus                 : ${engine.getConnectionState}")
        println(s"\tConnected at           : $connectionDate (Since: $connectionDuration)")
        println(s"\tAPI Version            | ${versions.apiVersion}")
        println(s"\tEngine Version         | ${versions.engineVersion}")
        println(s"\tImplementation Version | ${versions.implementationVersion}")
    }

    private def getDurationAsString(from: LocalDateTime): String = {
        var millis  = Duration.between(from, LocalDateTime.now()).toMillis
        var seconds = millis / 1000
        var minutes = seconds / 60
        var hours   = minutes / 60
        var days    = hours / 24
        var months  = days / 31
        val years   = months / 12

        millis %= 1000
        seconds %= 60
        minutes %= 60
        hours %= 24
        days %= 31
        months %= 12

        s"$years y, $months m, $days d, $hours h, $minutes m, $seconds s and $millis ms"
    }

}
