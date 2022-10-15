package dev.nyon.headquarters.gui.util

import kotlin.time.Duration

fun Int.toPrettyString(): String {
    if (this >= 1000000) return "${this.toString().dropLast(6)} M"
    if (this >= 1000) return "${this.toString().dropLast(3)} K"
    return this.toString()
}

fun Duration.distance(): String = if (this.inWholeDays <= -365) "${
    (this.inWholeDays / 365).toInt().toString().removePrefix("-")
} years ago"
else if (this.inWholeDays <= -30) "${
    (this.inWholeDays / 30).toInt().toString().removePrefix("-")
} months ago"
else if (this.inWholeDays <= -1) "${
    this.inWholeDays.toString().removePrefix("-")
} days ago"
else if (this.inWholeHours <= -1) "${
    this.inWholeHours.toString().removePrefix("-")
} hours ago"
else if (this.inWholeMinutes <= -1) "${
    this.inWholeMinutes.toString().removePrefix("-")
} minutes ago"
else "${this.inWholeSeconds.toString().removePrefix("-")} seconds ago"