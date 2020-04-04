import maven.report.Report
import maven.utils.Utils.getPomModel
import maven.utils.Utils.readPath

fun main(args : Array<String>) {
    val path = readPath()
    val pom = getPomModel(path)
    val report = Report()
    report.build(pom)
}