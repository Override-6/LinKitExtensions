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

import fr.linkit.api.connection.cache.repo.annotations.AnnotationHelper.MethodControl

case class Player(@MethodControl(localOnly = true) id: Int,
                  @MethodControl(localOnly = true) owner: String,
                  @MethodControl(localOnly = true) var name: String,
                  @MethodControl(localOnly = true) var x: Long,
                  @MethodControl(localOnly = true) var y: Long) extends Serializable {

    def this(other: Player) = {
        this(other.id, other.owner, other.name, other.x, other.y)
    }

    def getName: String = name

    @MethodControl(localOnly = true)
    override def toString: String = s"Player($id, $owner, $name, $x, $y)"
}
