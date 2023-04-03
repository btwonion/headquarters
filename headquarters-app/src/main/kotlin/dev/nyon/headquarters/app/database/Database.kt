package dev.nyon.headquarters.app.database

import dev.nyon.headquarters.app.dataDir
import dev.nyon.headquarters.app.json
import kotlinx.serialization.*
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.reflect.KClass

data class Database<T : @Serializable DatabaseEntry<*>>(
    val name: String,
    val migrations: Map<Int, KClass<out DatabaseEntry<*>>>,
    val dir: Path = dataDir
) {
    val dbFile: Path = dir.resolve("$name.json").also { if (it.notExists()) it.createFile() }

    @OptIn(InternalSerializationApi::class)
    inline fun <reified T> loadEntries(): List<T> {
        val fileText = dbFile.readText()
        val correctEntryVersion = migrations.keys.first()
        return runCatching outer@{
            json.parseToJsonElement(fileText).jsonArray.map { it.jsonObject }.mapNotNull {
                return@mapNotNull kotlin.runCatching {
                    val currentEntryVersion = it["version"]?.jsonPrimitive?.intOrNull ?: return@runCatching null
                    if (currentEntryVersion == correctEntryVersion) return@runCatching json.decodeFromString<T>(it.toString())
                    var currentObject =
                        json.decodeFromString(migrations[currentEntryVersion]!!.serializer(), it.toString())
                    while (currentObject.version != correctEntryVersion) {
                        currentObject = currentObject.migrateToNewerEntry() as DatabaseEntry<*>
                    }
                    return@runCatching currentObject as T
                }.getOrNull()
            }
        }.onFailure { dbFile.writeText(json.encodeToString(listOf<T>())) }.getOrDefault(listOf())
    }

    inline fun <reified T> saveEntries(entries: List<T>) = dbFile.writeText(json.encodeToString<List<T>>(entries))
}

interface DatabaseEntry<T> {
    val version: Int
    fun migrateToNewerEntry(): T
}

