package dev.nyon.headquarters.app.user

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserSettings : RealmObject {
    @PrimaryKey
    var id = 1

    var whiteTheme: Boolean = false
}