package dev.nyon.headquarters.gui.util

import kotlin.time.Duration

fun Int.toPrettyString(): String {
    if (this >= 1000000) return "${this.toString().dropLast(6)} M"
    if (this >= 1000) return "${this.toString().dropLast(3)} K"
    return this.toString()
}

fun Duration.distance(): String = if (this.inWholeDays <= -365) "${
    (this.inWholeDays / 365).toInt().toString().removePrefix("-")
} year${if ((this.inWholeDays / 365) < -1) "s" else ""} ago"
else if (this.inWholeDays <= -30) "${
    (this.inWholeDays / 30).toInt().toString().removePrefix("-")
} month${if ((this.inWholeDays / 30) < -1) "s" else ""} ago"
else if (this.inWholeDays <= -1) "${
    this.inWholeDays.toString().removePrefix("-")
} day${if (this.inWholeDays < -1) "s" else ""} ago"
else if (this.inWholeHours <= -1) "${
    this.inWholeHours.toString().removePrefix("-")
} hour${if (this.inWholeHours < -1) "s" else ""} ago"
else if (this.inWholeMinutes <= -1) "${
    this.inWholeMinutes.toString().removePrefix("-")
} minute${if (this.inWholeMinutes < -1) "s" else ""} ago"
else "${this.inWholeSeconds.toString().removePrefix("-")} second${if (this.inWholeSeconds < -1) "s" else ""} ago"