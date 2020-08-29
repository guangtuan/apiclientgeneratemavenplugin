package tech.igrant

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.InstantiationStrategy
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

import java.io.File

@Mojo(
        name = "generate",
        aggregator = true,
        executionStrategy = "always",
        inheritByDefault = false,
        instantiationStrategy = InstantiationStrategy.SINGLETON,
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDirectInvocation = true
)
class GenerateMojo : AbstractMojo() {
    @Parameter(name = "type", defaultValue = "typeScript")
    private val type: String? = null

    @Parameter(defaultValue = "\${plugin}", readonly = true)
    private val plugin: PluginDescriptor? = null

    @Parameter(defaultValue = "\${project.basedir}", readonly = true)
    private val basedir: File? = null

    @Parameter(defaultValue = "\${project.build.directory}", readonly = true)
    private val target: File? = null

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        println(type)
        println(plugin!!.name)
        println(basedir!!.absolutePath)
        println(target!!.absolutePath)
        println(123)
    }

}