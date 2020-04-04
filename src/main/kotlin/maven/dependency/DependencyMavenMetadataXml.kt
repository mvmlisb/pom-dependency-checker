package maven.dependency

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.NullPointerException

class DependencyMavenMetadataXml internal constructor(groupId: String, artifactId: String) {

    private val uri: String

    private val xml: Document

    val latestVersion: String

    init {
        uri = buildUri(groupId, artifactId)
        xml = Jsoup.connect(uri).get()
        latestVersion = getLatestVersion(xml)
    }

    companion object{

        private const val UNKNOWN_VERSION = "Unknown"

        private fun buildUri(groupId: String, artifactId: String): String {
            val formattedGroupId = groupId.replace(".", "/")
            return "https://repo1.maven.org/maven2/$formattedGroupId/$artifactId/maven-metadata.xml"
        }

        private fun getLatestVersion(xml: Document): String {
            return try {
                xml.select("latest").text()
            } catch (e: NullPointerException) {
                UNKNOWN_VERSION
            }
        }

    }

    fun isLatestVersionUnknown() = latestVersion == UNKNOWN_VERSION

}