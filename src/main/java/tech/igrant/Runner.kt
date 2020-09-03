package tech.igrant

import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class Runner {

    companion object {

        private const val SUFFIX_CLASS = ".class"

        private val LIST_OF_REQUEST_MAPPING = listOf(
                ANNA.REQUEST_MAPPING,
                ANNA.GET_MAPPING,
                ANNA.POST_MAPPING,
                ANNA.PUT_MAPPING,
                ANNA.PATCH_MAPPING,
                ANNA.DELETE_MAPPING
        )

        fun workWithTargetDir(targetDir: File, type: String?, packages: Array<String>, log: Log, mavenProject: MavenProject?) {
            log.info("handle file in ${targetDir.absolutePath}, type: $type, project: ${mavenProject?.name}")
            val runtimeList = mavenProject?.runtimeClasspathElements?.toTypedArray()
            runtimeList?.let { it ->
                val urlClassLoader = URLClassLoader(it.map { d -> File(d).toURI().toURL() }.toTypedArray(), Thread.currentThread().contextClassLoader)
                urlClassLoader.urLs.forEach { log.info("url of classLoader: $it") }
                val dir = File(targetDir, "classes")
                log.info(dir.absolutePath)

                val businessCode = Files.walk(Paths.get(dir.toURI()))
                        .map { p -> p.toString() }
                        .filter { p -> p.endsWith(SUFFIX_CLASS) }
                        .map { p -> p.replace("${dir.absolutePath}/", "").slashToDot().replace(SUFFIX_CLASS, "") }
                        .filter { n -> packages.toList().stream().anyMatch { n.startsWith(it) } }
                        .map { name -> urlClassLoader.loadClass(name) }

                val restControllers = businessCode.filter { c ->
                    c.annotations.any { a ->
                        a.annotationClass.java == urlClassLoader.loadClass(ANNA.REST_CONTROLLER.canonicalName)
                    }
                }.toList()

                restControllers.forEach { log.info("restController: ${it.canonicalName}") }

                val resultList = restControllers.fold(mutableListOf(), { acc: MutableList<HttpContextDesc>, restController: Class<*> ->
                    restController.annotations.find {
                        LIST_OF_REQUEST_MAPPING.any { anna -> urlClassLoader.loadClass(anna.canonicalName) == it.annotationClass.java }
                    }?.let { annaOnClass ->
                        //                        log.info("find annaOnClass: ${annaOnClass.annotationClass.java.canonicalName} on class ${restController.canonicalName}}")
                        val annotationInstanceValue = restController.getAnnotation(annaOnClass.annotationClass.java)
                        val commonHttpMethod = mapHttpMethod(annaOnClass, urlClassLoader, annotationInstanceValue)
//                        annaOnClass.javaClass.methods.forEach { m -> log.info("function name: ${m.name} of annaOnClass: ${annaOnClass.annotationClass.java.canonicalName}") }
                        val baseUrl = (annaOnClass.javaClass.methods.first { m -> m.name == "value" }.invoke(annotationInstanceValue) as Array<*>)[0] as String
                        acc.addAll(
                                // 只有打了 *Mapping 注解的函数，才需要返回一个对应的 http 请求
                                restController.methods.mapNotNull { method ->
                                    method.annotations.find {
                                        LIST_OF_REQUEST_MAPPING.any { anna -> urlClassLoader.loadClass(anna.canonicalName) == it.annotationClass.java }
                                    }?.let { annOnFunction ->
                                        // 只有打了 *Mapping 注解的函数，才需要返回一个对应的 http 请求
                                        val annOnFunctionInstance = method.getAnnotation(annOnFunction.annotationClass.java)
                                        val httpMethod = commonHttpMethod
                                                ?: mapHttpMethod(annOnFunction, urlClassLoader, annOnFunctionInstance)
                                        log.info("annOnFunctionInstance: $annOnFunctionInstance")
                                        val asArray = annOnFunction.javaClass.methods.first { method -> method.name == "value" }.invoke(annOnFunctionInstance) as Array<*>
                                        val url = merge(baseUrl, getPath(asArray))
                                        HttpContextDesc(
                                                url = url,
                                                httpMethod = httpMethod,
                                                responseBodyClass = method.genericReturnType.typeName,
                                                listOfParams = mapToParamsDesc(method = method)
                                        )
                                    }
                                }.toList())
                    }
                    acc
                })

                resultList.forEach { r -> log.info(r.toString()) }
            }
        }

        private fun mapToParamsDesc(method: Method): List<ParamsDesc> {
            return method.parameters.map { parameter ->
                ParamsDesc(
                        paramsClass = parameter.type.canonicalName,
                        require = false,
                        asQueryParamKey = "",
                        name = parameter.name
                )
            }
        }

        private fun getPath(asArray: Array<*>): String {
            return if (asArray.isEmpty()) {
                ""
            } else {
                asArray[0] as String
            }
        }

        private fun merge(baseUrl: String, path: String): String {
            return listOf(baseUrl.removeSuffix("/"), path.removePrefix("/")).joinToString("/")
        }

        private fun mapHttpMethod(anna: Annotation, urlClassLoader: URLClassLoader, annotationInstanceValue: Annotation): HttpMethod? {
            if (anna.annotationClass.java == urlClassLoader.loadClass(ANNA.REQUEST_MAPPING.canonicalName)) {
                val retVal = anna.javaClass.methods.first { m -> m.name == "method" }.invoke(annotationInstanceValue)
                if (retVal is Array<*>) {
                    if (retVal.isNotEmpty()) {
                        return HttpMethod.valueOf(retVal[0].toString())
                    }
                }
            }
            if (anna.annotationClass.java == urlClassLoader.loadClass(ANNA.GET_MAPPING.canonicalName)) {
                return HttpMethod.GET
            }
            if (anna.annotationClass.java == urlClassLoader.loadClass(ANNA.POST_MAPPING.canonicalName)) {
                return HttpMethod.POST
            }
            if (anna.annotationClass.java == urlClassLoader.loadClass(ANNA.PUT_MAPPING.canonicalName)) {
                return HttpMethod.PUT
            }
            if (anna.annotationClass.java == urlClassLoader.loadClass(ANNA.PATCH_MAPPING.canonicalName)) {
                return HttpMethod.PATCH
            }
            if (anna.annotationClass.java == urlClassLoader.loadClass(ANNA.DELETE_MAPPING.canonicalName)) {
                return HttpMethod.DELETE
            }
            return null
        }

    }

}