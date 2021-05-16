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

case class Player(id: Int,
                  owner: String,
                  var name: String,
                  var x: Long,
                  var y: Long) extends Serializable {

    def this(other: Player) = {
        this(other.id, other.owner, other.name, other.x, other.y)
    }

    def getName: String = name

    def setX(x: Long): Unit = this.x = x

    def setY(y: Long): Unit = this.y = y

    override def toString: String = s"Player($id, $owner, $name, $x, $y)"
}