package dev.nyon.headquarters.app.profile

import io.realm.kotlin.types.EmbeddedRealmObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Project() : EmbeddedRealmObject {
    @SerialName("version_id")
    var versionID: String = ""
    var projectID: String = ""
    var enabled: Boolean = true
}