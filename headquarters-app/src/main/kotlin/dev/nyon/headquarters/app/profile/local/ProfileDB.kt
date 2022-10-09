package dev.nyon.headquarters.app.profile.local

import dev.nyon.headquarters.app.profile.models.LocalProfile
import dev.nyon.headquarters.app.runningDir
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

val realm = run {
    val config = RealmConfiguration.Builder(setOf(LocalProfile::class))
        .directory("${runningDir.toAbsolutePath().toString()}/data/db/").name("headquarters_db").build()
    Realm.open(config)
}