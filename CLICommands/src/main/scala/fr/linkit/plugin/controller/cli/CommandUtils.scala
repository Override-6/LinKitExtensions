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

package fr.linkit.plugin.controller.cli

object CommandUtils {

    def checkArgsContains(expected: String*)(implicit args: Array[String]): Unit = {
        val success = expected.forall(args.contains)
        if (success)
            return
        val errorMsg = s"missing or wrong argument in command syntax. Expected : ${expected.mkString(" and ")}"
        throw CommandException(errorMsg)
    }


    def argAfter(ref: String)(implicit args: Array[String]): String =
        args(args.indexOf(ref) + 1)

}
