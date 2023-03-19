package dev.nyon.headquarters.app.util

import kotlin.random.Random

fun generateID(length: Int = 8) : String {
    val random = Random
    var id = ""

    repeat(length) {
        val letterType = random.nextInt(0, 2)
        val asciiCode = if (letterType == 0) random.nextInt(65, 91) else random.nextInt(97, 123)

        id += asciiCode.toChar()
    }

    return id
}