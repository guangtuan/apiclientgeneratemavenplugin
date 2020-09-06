package tech.igrant

import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import java.io.File
import java.net.URLClassLoader

class Runner {

    companion object {

        fun workWithTargetDir(type: String?, targetDir: File, packages: Array<String>, mavenProject: MavenProject?, log: Log) {
            log.info("handle file in ${targetDir.absolutePath}, type: $type, project: ${mavenProject?.name}")
            val runtimeList = mavenProject?.runtimeClasspathElements?.toTypedArray()
            runtimeList?.let { it ->
                val urlClassLoader = URLClassLoader(it.map { d -> File(d).toURI().toURL() }.toTypedArray(), Thread.currentThread().contextClassLoader)
                urlClassLoader.urLs.forEach { log.info("url in classLoader: $it") }
                val apiDescriptions = ApiDescGetter.getApiDescriptions(File(targetDir, "classes"), log, packages, urlClassLoader)
                apiDescriptions.forEach { r -> log.info(r.toString()) }
            }
        }

    }

}