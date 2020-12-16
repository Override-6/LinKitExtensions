package fr.`override`.linkkit.`extension`.controller.cli

trait CommandExecutor {

    def execute(implicit args: Array[String]): Unit

}
