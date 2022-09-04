package dev.nyon.headquarter.api.networking

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import dev.nyon.headquarter.api.common.InternalHeadquarterAPI
import dev.nyon.headquarter.api.group.Group
import dev.nyon.headquarter.api.group.Template
import kotlinx.coroutines.CoroutineScope
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

@InternalHeadquarterAPI
val mongoClient =
    KMongo.createClient(MongoClientSettings.builder().applyConnectionString(connectionString).build()).coroutine

@InternalHeadquarterAPI
val db = mongoClient.getDatabase(System.getenv("MONGO_DATABASE"))

lateinit var groups: CoroutineCollection<Group>
lateinit var templates: CoroutineCollection<Template>
@InternalHeadquarterAPI
suspend fun CoroutineScope.initMongoDbs() {
    groups = db.getAndCreateCollection("groups")
    templates = db.getAndCreateCollection("templates")
}