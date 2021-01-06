package fr.`override`.linkit.`extension`.cloud.data

import java.sql.Connection

import fr.`override`.linkit.api.`extension`.RelayProperties

class RelayStoredProperties(connection: Connection, relayProperties: RelayProperties) {

    //init table
    connection.createStatement()
            .execute("CREATE TABLE IF NOT EXISTS properties(name VARCHAR PRIMARY KEY, value VARCHAR)")

    private val preparedSet = connection.prepareStatement("INSERT OR REPLACE INTO properties VALUES(?, ?)")
    private val preparedGet = connection.prepareStatement("SELECT * FROM properties")

    def store(): Unit = {
        println("STORING")
        relayProperties.foreach((name, value) => {
            preparedSet.setString(1, name)
            preparedSet.setString(2, value.toString)
            preparedSet.addBatch()
        })
        preparedSet.executeUpdate()
        println("STORED")
    }

    update() //automatically update
    def update(): Unit = {
        val set = preparedGet.executeQuery()
        while (set.next()) {
            val name = set.getString(1)
            val value = set.getString(2)
            relayProperties.putProperty(name, value)
        }
    }

}
