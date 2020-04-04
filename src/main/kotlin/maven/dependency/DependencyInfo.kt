package maven.dependency

import org.apache.maven.model.Dependency
import java.util.*

class DependencyInfo (dependency: Dependency, properties: Properties) {

    private val mavenMetadataXml: DependencyMavenMetadataXml

    val groupId: String

    val artifactId: String

    val currentVersion: String

    val latestVersion: String

    init {
        groupId = dependency.groupId
        artifactId = dependency.artifactId
        currentVersion = getCurrentVersion(dependency, properties)
        mavenMetadataXml = DependencyMavenMetadataXml(groupId, artifactId)
        latestVersion = mavenMetadataXml.latestVersion
    }

    companion object {

        private fun getCurrentVersion(dependency: Dependency, properties: Properties): String {
            return if (dependency.version.contains(".version")) {
                getVersionFromProperties(dependency, properties)
            }
            else dependency.version
        }

        private fun getVersionFromProperties(dependency: Dependency, properties: Properties): String {
            val key = dependency.version.removeSurrounding("\${", "}")
            return properties.getProperty(key)
        }

    }

    fun isCurrentVersionLatest(): Boolean? {
        if(mavenMetadataXml.isLatestVersionUnknown() || isCurrentVersionContainsCharsForHardRequirement()) {
            return null
        }
        return currentVersion == latestVersion
    }

    private fun isCurrentVersionContainsCharsForHardRequirement(): Boolean {
        return "[\\[\\]()]".toRegex() in currentVersion
    }

}