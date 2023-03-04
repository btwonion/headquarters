package dev.nyon.headquarters.server.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import dev.nyon.headquarters.api.Profile
import dev.nyon.headquarters.api.user.User
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

var users: CoroutineCollection<User> = db.getCollection("users")
var profiles: CoroutineCollection<Profile> = db.getCollection("profiles")