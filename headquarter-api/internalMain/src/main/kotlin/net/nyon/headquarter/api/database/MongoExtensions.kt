package net.nyon.headquarter.api.database

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

suspend fun <T : @Serializable Any> CoroutineCollection<T>.retrieveOne(key: String, value: String): T? {
    return this.find(Filters.eq(key, value)).first()
}

fun <T : @Serializable Any> CoroutineCollection<T>.retrieveMany(key: String, value: String): Flow<T> {
    return this.find(Filters.eq(key, value)).toFlow()
}

suspend fun <T : @Serializable Any> CoroutineCollection<T>.replace(key: String, value: String, doc: T) {
    this.replaceOne(Filters.eq(key, value), doc)
}

suspend fun CoroutineDatabase.getAndCreateCollection(name: String): CoroutineCollection<@Serializable Any> {
    return if (this.listCollectionNames().contains(name)) this.getCollection(name)
    else {
        this.createCollection(name)
        this.getCollection(name)
    }
}