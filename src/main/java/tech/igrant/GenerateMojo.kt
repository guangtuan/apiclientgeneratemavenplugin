package tech.igrant

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.*
import org.apache.maven.project.MavenProject
import java.io.File
import kotlin.jvm.Throws

@Mojo(
        name = "generate",
        instantiationStrategy = InstantiationStrategy.SINGLETON,
        defaultPhase = LifecyclePhase.INSTALL,
        requiresDirectInvocation = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME
)
class GenerateMojo : AbstractMojo() {
    @Parameter(name = "type", defaultValue = "typeScript")
    private val type: String? = null

    @Parameter(name = "packages")
    private val packages: Array<String>? = null

    @Parameter(defaultValue = "\${plugin}", readonly = true)
    private val plugin: PluginDescriptor? = null

    @Parameter(defaultValue = "\${project.basedir}", readonly = true)
    private val basedir: File? = null

    @Parameter(defaultValue = "\${project.build.directory}", readonly = true)
    private val target: File? = null

    @Parameter(defaultValue = "\${project}", readonly = true)
    private val mavenProject: MavenProject? = null

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        target?.let {
            Runner.workWithTargetDir(
                    type, it, packages!!, mavenProject, log)
        }
    }

}