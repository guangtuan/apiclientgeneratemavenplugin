package tech.igrant

import org.apache.maven.plugin.logging.Log
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.toList

class ApiDescGetter {

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

        fun getApiDescriptions(targetDir: File, log: Log, packages: Array<String>, classLoader: ClassLoader): MutableList<ApiDescription> {
            val businessCode = loadBusinessCode(targetDir, log, packages, classLoader)

            val restControllers = businessCode.filter { c ->
                c.annotations.any { a ->
                    a.annotationClass.java == classLoader.loadClass(ANNA.REST_CONTROLLER.canonicalName)
                }
            }.toList()

            restControllers.forEach { log.info("restController: ${it.canonicalName}") }

            return restControllers.fold(mutableListOf(), { acc: MutableList<ApiDescription>, restController: Class<*> ->
                restController.annotations.find {
                    LIST_OF_REQUEST_MAPPING.any { anna -> classLoader.loadClass(anna.canonicalName) == it.annotationClass.java }
                }?.let { annaOnClass ->
                    val annotationInstanceValue = restController.getAnnotation(annaOnClass.annotationClass.java)
                    val commonHttpMethod = mapHttpMethod(annaOnClass, classLoader, annotationInstanceValue)
                    val baseUrl = (annaOnClass.javaClass.methods.first { m -> m.name == "value" }.invoke(annotationInstanceValue) as Array<*>)[0] as String
                    acc.addAll(
                            // 只有打了 *Mapping 注解的函数，才需要返回一个对应的 http 请求
                            restController.methods.mapNotNull { method ->
                                method.annotations.find {
                                    LIST_OF_REQUEST_MAPPING.any { anna -> classLoader.loadClass(anna.canonicalName) == it.annotationClass.java }
                                }?.let { annOnFunction ->
                                    // 只有打了 *Mapping 注解的函数，才需要返回一个对应的 http 请求
                                    val annOnFunctionInstance = method.getAnnotation(annOnFunction.annotationClass.java)
                                    val httpMethod = commonHttpMethod
                                            ?: mapHttpMethod(annOnFunction, classLoader, annOnFunctionInstance)
                                    log.info("annOnFunctionInstance: $annOnFunctionInstance")
                                    val asArray = annOnFunction.javaClass.methods.first { method -> method.name == "value" }.invoke(annOnFunctionInstance) as Array<*>
                                    val url = merge(baseUrl, getPath(asArray))
                                    ApiDescription(
                                            url = url,
                                            httpMethod = httpMethod,
                                            name = method.name,
                                            responseBodyClass = method.genericReturnType.typeName,
                                            listOfParams = mapToParamsDesc(method = method, classLoader = classLoader)
                                    )
                                }
                            }.toList())
                }
                acc
            })
        }

        private fun mapToParamsDesc(method: Method, classLoader: ClassLoader): List<ParamsDesc> {
            val parameterAnnotations = method.parameterAnnotations
            return method.parameters.mapIndexed { index, parameter ->
                val arrayOfAnnotations = parameterAnnotations[index]
                for (anna in arrayOfAnnotations) {
                    if (anna.annotationClass.java == classLoader.loadClass(ANNA.PATH_VARIABLE.canonicalName)) {
                        return@mapIndexed ParamsDesc(
                                paramsClass = parameter.type.canonicalName,
                                require = true,
                                paramType = ParamType.PATH,
                                name = anna.javaClass.methods.find { m -> m.name == "value" }?.invoke(anna) as String
                        )
                    }
                    if (anna.annotationClass.java == classLoader.loadClass(ANNA.REQUEST_PARAM.canonicalName)) {
                        return@mapIndexed ParamsDesc(
                                paramsClass = parameter.type.canonicalName,
                                require = anna.javaClass.methods.find { m -> m.name == "required" }?.invoke(anna) as Boolean,
                                paramType = ParamType.QUERY,
                                name = anna.javaClass.methods.find { m -> m.name == "value" }?.invoke(anna) as String
                        )
                    }
                    if (anna.annotationClass.java == classLoader.loadClass(ANNA.REQUEST_BODY.canonicalName)) {
                        return@mapIndexed ParamsDesc(
                                paramsClass = parameter.type.canonicalName,
                                require = anna.javaClass.methods.find { m -> m.name == "required" }?.invoke(anna) as Boolean,
                                paramType = ParamType.BODY,
                                name = parameter.type.canonicalName.classNameToVariableName()
                        )
                    }
                }
                ParamsDesc(
                        paramsClass = parameter.type.canonicalName,
                        require = false,
                        paramType = ParamType.BODY,
                        name = parameter.type.canonicalName.classNameToVariableName()
                )
            }
        }

        private fun mapHttpMethod(anna: Annotation, classLoader: ClassLoader, annotationInstanceValue: Annotation): HttpMethod? {
            if (anna.annotationClass.java == classLoader.loadClass(ANNA.REQUEST_MAPPING.canonicalName)) {
                val retVal = anna.javaClass.methods.first { m -> m.name == "method" }.invoke(annotationInstanceValue)
                if (retVal is Array<*> && retVal.isNotEmpty()) {
                    return HttpMethod.valueOf(retVal[0].toString())
                }
            }
            if (anna.annotationClass.java == classLoader.loadClass(ANNA.GET_MAPPING.canonicalName)) {
                return HttpMethod.GET
            }
            if (anna.annotationClass.java == classLoader.loadClass(ANNA.POST_MAPPING.canonicalName)) {
                return HttpMethod.POST
            }
            if (anna.annotationClass.java == classLoader.loadClass(ANNA.PUT_MAPPING.canonicalName)) {
                return HttpMethod.PUT
            }
            if (anna.annotationClass.java == classLoader.loadClass(ANNA.PATCH_MAPPING.canonicalName)) {
                return HttpMethod.PATCH
            }
            if (anna.annotationClass.java == classLoader.loadClass(ANNA.DELETE_MAPPING.canonicalName)) {
                return HttpMethod.DELETE
            }
            return null
        }

        private fun loadBusinessCode(targetDir: File, log: Log, packages: Array<String>, classLoader: ClassLoader): List<Class<*>> {
            log.info("source code classes path: ${targetDir.absolutePath}")

            return Files.walk(Paths.get(targetDir.toURI()))
                    .map { p -> p.toString() }
                    .filter { p -> p.endsWith(SUFFIX_CLASS) }
                    .map { p -> p.replace("${targetDir.absolutePath}/", "").slashToDot().replace(SUFFIX_CLASS, "") }
                    .filter { n -> packages.toList().stream().anyMatch { n.startsWith(it) } }
                    .map { name -> classLoader.loadClass(name) }
                    .toList()
        }

        private fun getPath(asArray: Array<*>): String {
            return if (asArray.isEmpty()) {
                ""
            } else {
                asArray[0] as String
            }
        }

        private fun merge(baseUrl: String, path: String): String {
            return listOf(baseUrl.removeSuffix("/"), path.removePrefix("/")).filter { s -> s.isNotEmpty() }.joinToString("/")
        }

    }

}