package net.nyon.headquarter.api.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val connectionString = ConnectionString(
    "mongodb://${System.getenv("MONGO_USERNAME")}:${System.getenv("MONGO_PASSWORD")}@${
        System.getenv(
            "MONGO_ADDRESS"
        )
    }:${System.getenv("MONGO_PORT")}/?authSource=${System.getenv("MONGO_DATABASE")}"
)
val mongoClient =
    KMongo.createClient(MongoClientSettings.builder().applyConnectionString(connectionString).build()).coroutine

val db = mongoClient.getDatabase(System.getenv("MONGO_DATABASE"))

lateinit var nodes: CoroutineCollection<@Serializable Any>

suspend fun initMongoDbs() {
    nodes = db.getAndCreateCollection("nodes")
}