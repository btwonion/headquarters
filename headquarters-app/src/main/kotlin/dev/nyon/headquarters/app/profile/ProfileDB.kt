package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.runningDir
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

val realm = run {
    val config =
        RealmConfiguration.Builder(setOf(Profile::class)).directory("${runningDir.toAbsolutePath()}/data/db/")
            .name("headquarters_db").build()
    Realm.open(config)
}