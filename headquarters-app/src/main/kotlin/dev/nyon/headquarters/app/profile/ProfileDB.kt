package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.dataDir
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

val profileDB = run {
    val config =
        RealmConfiguration.Builder(setOf(Profile::class, Project::class)).directory("${dataDir.toAbsolutePath()}/db/")
            .name("headquarters_profiles").build()
    Realm.open(config)
}

suspend fun updateProfile(id: String, realmCallback: MutableRealm.(profile: Profile) -> Unit) {
    profileDB.write {
        val foundProfile = this@write.query<Profile>("profileID == $0", id).first().find()!!
        realmCallback(foundProfile)
    }
}

suspend fun getProfile(id: String): Profile? =
    profileDB.query<Profile>("profileID == $0", id).first().find().also {
        it?.initLoaderProfile()
        it?.initMinecraftVersionPackage()
    }