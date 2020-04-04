package maven.utils

import org.apache.maven.model.Model
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.Charsets.UTF_8

object Utils{

    fun getCurrentDate(pattern: String): String = SimpleDateFormat(pattern).format(Date())

    fun resourceAsText(path: String): String = javaClass.getResource(path).readText(UTF_8)

    fun booleanToYesNoOrUnknown(value: Boolean?): String {
        return when (value) {
            true -> "Yes"
            false -> "No"
            null -> "Unknown"
        }
    }

    fun readPath(): String {
        println("Enter path to pom.xml:")
        return readLine().orEmpty()
    }

    fun getPomModel(path: String): Model {
        val file = getPomFile(path)
        val fileReader = FileReader(file)
        val mavenXpp3Reader = MavenXpp3Reader()
        return mavenXpp3Reader.read(fileReader)
    }

    private fun getPomFile(path: String): File {
        val file = File(path)
        require(file.exists()) { "File is not exist." }
        require(file.name.endsWith("pom.xml")) { "File is not pom.xml." }
        return file
    }
}
