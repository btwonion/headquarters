package dev.nyon.headquarters.server.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters
import dev.nyon.headquarters.api.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val databaseScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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

var profiles: CoroutineCollection<Profile> = db.getCollection("profiles")
suspend fun initMongo() {
    profiles = db.getAndCreateCollection("profiles")
}

suspend fun <T : @Serializable Any> CoroutineCollection<T>.retrieveOne(key: String, value: String): T? =
    this.find(Filters.eq(key, value)).first()

fun <T : @Serializable Any> CoroutineCollection<T>.retrieveMany(key: String, value: String): Flow<T> =
    this.find(Filters.eq(key, value)).toFlow()

suspend fun <T : @Serializable Any> CoroutineCollection<T>.replace(key: String, value: String, doc: T) =
    this.replaceOne(Filters.eq(key, value), doc)

suspend inline fun <reified T : @Serializable Any> CoroutineDatabase.getAndCreateCollection(name: String): CoroutineCollection<T> =
    if (this.listCollectionNames().contains(name)) this.getCollection(name)
    else {
        this.createCollection(name)
        this.getCollection(name)
    }