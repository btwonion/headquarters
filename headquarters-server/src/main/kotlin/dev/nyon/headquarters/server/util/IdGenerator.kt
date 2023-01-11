package dev.nyon.headquarters.server.util

import dev.nyon.headquarters.api.IdHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import kotlin.random.Random

suspend inline fun <reified T : IdHolder> generateAndCheckID(length: Int, collection: CoroutineCollection<T>): String =
    withContext(Dispatchers.IO) {
        var id = generateID(length)
        var found = false

        while (!found) {
            if (collection.findOne(IdHolder::id eq id) != null) id = generateID(length)
            else found = true
        }

        id
    }

fun generateID(length: Int): String {
    val random = Random
    var id = ""

    repeat(length) {
        val letterType = random.nextInt(0, 2)
        val asciiCode = if (letterType == 0) random.nextInt(65, 91) else random.nextInt(97, 123)

        id += asciiCode.toChar()
    }

    return id
}