package dev.nyon.headquarters.gui.util

fun Int.toPrettyString(): String {
    if (this >= 1000000) return "${this.toString().dropLast(6)} M"
    if (this >= 1000) return "${this.toString().dropLast(3)} K"
    return this.toString()
}