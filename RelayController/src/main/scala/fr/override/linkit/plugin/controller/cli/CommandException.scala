package fr.`override`.linkit.plugin.controller.cli

case class CommandException(msg: String) extends RuntimeException(msg)
