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

package fr.linkit.plugin.controller.cli

import java.util.concurrent.PriorityBlockingQueue
import scala.io.StdIn

object InputConsole {

    private val ticketQueue = new PriorityBlockingQueue[InputRequestTicket]()

    def ask(msg: String, possibleResponses: String*): String = {
        println(msg)
        var input = requestNextInput(10)
        if (possibleResponses.isEmpty)
            input
        else {
            while (possibleResponses.forall(_.toLowerCase != input)) {
                println(msg)
                input = requestNextInput(10)
            }
            input
        }
    }

    def requestNextInput(priority: Int = 0): String = {
        val requestTicket = new InputRequestTicket(defNextPriority(priority))
        ticketQueue.add(requestTicket)
        requestTicket.getLine
    }

    private def defNextPriority(priority: Int): Int = {
        var queuePriority = priority
        ticketQueue.forEach(incPriority)

        def incPriority(ticket: InputRequestTicket): Unit = {
            if (ticket.priority < priority)
                return
            if (ticket.priority == queuePriority)
                queuePriority += 1
        }

        queuePriority
    }


    private def start(): Unit = {
        val consoleThread = new Thread(() => {
            while (true) {
                val ticket = ticketQueue.take()
                val line = StdIn.readLine()
                ticket.setLine(line)
            }
        })
        consoleThread.setName("Console Inputs Queue")
        consoleThread.setDaemon(true)
        consoleThread.start()
    }

    start()

    class InputRequestTicket(val priority: Int) extends Comparable[InputRequestTicket] {
        private val threadOwner = Thread.currentThread()
        @volatile private var line: String = _

        def getLine: String = {
            if (line == null)
                synchronized {
                    wait()
                }
            line
        }

        def setLine(line: String): Unit = {
            this.line = line
            synchronized {
                notify()
            }
        }

        override def toString: String = s"owner : $threadOwner"

        override def compareTo(o: InputRequestTicket): Int =
            o.priority - priority
    }

}
