package maven.report

import maven.dependency.DependencyInfo
import maven.utils.Utils.booleanToYesNoOrUnknown
import maven.utils.Utils.getCurrentDate
import maven.utils.Utils.resourceAsText
import org.apache.maven.model.Model
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

class Report {

    private val content: Document

    init {
        content = getContentTemplate()
        embedStyles(content)
    }

    companion object {

        private fun getContentTemplate(): Document {
            val resource = resourceAsText("/report_template.html")
            return Jsoup.parse(resource)
        }

        private fun embedStyles(content: Document) {
            val resource = resourceAsText("/report_styles.css")
            content.select("style").append(resource)
        }

    }

    fun build(pom: Model){
        println("Building report...")
        fillProjectInfo(pom)
        fillDependenciesInfo(pom)
        writeToFile().also {
            println("Your report is ready. Path:\n${it.absoluteFile}")
        }
    }

    private fun fillProjectInfo(pom: Model) {
        content.getElementById("group-id").appendText(pom.groupId)
        content.getElementById("artifact-id").appendText(pom.artifactId)
        content.getElementById("version").appendText(pom.version)
    }

    private fun fillDependenciesInfo(pom: Model) {
        val tbody = content.select("tbody")
        val rows = buildRowsWithDependencyInfo(pom)
        tbody.append(rows)
    }

    private fun buildRowsWithDependencyInfo(pom: Model): String {
        val sb = StringBuilder()
        pom.dependencies.forEach {
            val info = DependencyInfo(it, pom.properties)
            val row = buildRowWithDependencyInfo(info)
            sb.appendln(row)
        }
        return sb.toString()

    }

    private fun buildRowWithDependencyInfo(info: DependencyInfo): String {
        val isCurrentVersionLatest = booleanToYesNoOrUnknown(info.isCurrentVersionLatest())
        return StringBuilder().apply {
            appendln("<tr>")
            appendln("<td>${info.groupId}</td>")
            appendln("<td>${info.artifactId}</td>")
            appendln("<td>${info.currentVersion}</td>")
            appendln("<td>${info.latestVersion}</td>")
            appendln("<td>${isCurrentVersionLatest}</td>")
            appendln("</tr>")
        }.toString()
    }

    private fun writeToFile(): File {
        val fileName = createFileName()
        val file = File(fileName)
        file.printWriter().use { it.println(content.toString()) }
        return file
    }

    private fun createFileName(): String {
        val currentDate = getCurrentDate("yyyyMMdd_HHmmss")
        return "report_$currentDate.html"
    }

}