package tech.igrant

import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths

class Runner {

    companion object {

        private const val SUFFIX_CLASS = ".class"

        fun workWithTargetDir(targetDir: File, type: String?, packages: Array<String>, log: Log, mavenProject: MavenProject?) {
            log.info("handle file in ${targetDir.absolutePath}, type: $type, project: ${mavenProject?.name}")
            val runtimeList = mavenProject?.runtimeClasspathElements?.toTypedArray()
            runtimeList?.let { it ->
                val urlClassLoader = URLClassLoader(it.map { d -> File(d).toURI().toURL() }.toTypedArray(), Thread.currentThread().contextClassLoader)
                urlClassLoader.urLs.forEach { log.info("url of classLoader: $it") }
                val dir = File(targetDir, "classes")
                log.info(dir.absolutePath)
                Files.walk(Paths.get(dir.toURI()))
                        .map { p -> p.toString() }
                        .filter { p -> p.endsWith(SUFFIX_CLASS) }
                        .map { p -> p.replace("${dir.absolutePath}/", "").slashToDot().replace(SUFFIX_CLASS, "") }
                        .filter { n -> packages.toList().stream().anyMatch { n.startsWith(it) } }
                        .map { name -> urlClassLoader.loadClass(name) }
                        .forEach { log.info(it.name) }
            }
        }

    }

}