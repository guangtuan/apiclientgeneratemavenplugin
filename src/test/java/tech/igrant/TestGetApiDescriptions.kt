package tech.igrant

import org.junit.Assert
import org.junit.Test
import java.io.File

class TestGetApiDescriptions {

    private val expected = listOf(
            ApiDescription(
                    name = "list",
                    httpMethod = HttpMethod.GET,
                    responseBodyClass = "org.springframework.http.ResponseEntity<java.util.List<tech.igrant.business.model.Pet>>",
                    listOfParams = listOf(),
                    url = "api/v1/pets"
            ),
            ApiDescription(
                    name = "queryByName",
                    httpMethod = HttpMethod.GET,
                    responseBodyClass = "org.springframework.http.ResponseEntity<java.util.List<tech.igrant.business.model.Pet>>",
                    listOfParams = listOf(
                            ParamsDesc(
                                    name = "name",
                                    value = null,
                                    paramsClass = JavaBaseType.STRING.canonicalName,
                                    require = true,
                                    paramType = ParamType.QUERY
                            )
                    ),
                    url = "api/v1/pets"
            ),
            ApiDescription(
                    name = "create",
                    httpMethod = HttpMethod.POST,
                    responseBodyClass = "org.springframework.http.ResponseEntity<tech.igrant.business.model.Pet>",
                    listOfParams = listOf(
                            ParamsDesc(
                                    name = "petDto",
                                    paramsClass = "tech.igrant.business.model.PetDto",
                                    require = true,
                                    paramType = ParamType.BODY
                            )
                    ),
                    url = "api/v1/pets"
            ),
            ApiDescription(
                    name = "queryByCondition",
                    httpMethod = HttpMethod.POST,
                    responseBodyClass = "org.springframework.http.ResponseEntity<java.util.List<tech.igrant.business.model.Pet>>",
                    listOfParams = listOf(
                            ParamsDesc(
                                    name = "queryCondition",
                                    paramsClass = "tech.igrant.business.model.QueryCondition",
                                    require = false,
                                    paramType = ParamType.BODY
                            )
                    ),
                    url = "api/v1/pets/query"
            ),
            ApiDescription(
                    name = "del",
                    httpMethod = HttpMethod.DELETE,
                    responseBodyClass = "org.springframework.http.ResponseEntity<java.lang.Object>",
                    listOfParams = listOf(
                            ParamsDesc(
                                    name = "id",
                                    paramsClass = JavaBaseType.long.canonicalName,
                                    require = true,
                                    paramType = ParamType.PATH
                            )
                    ),
                    url = "api/v1/pets/{id}"
            )
    )

    @Test
    fun getApiDesc() {

        val classPathsKey = "java.class.path"
        val classPaths = System.getProperty(classPathsKey)
        val path = classPaths.split(":").first { !it.endsWith(".jar") && it.contains("test-classes") }
        println("path is $path")
        val targetDir = File(path)
        val actual = ApiDescGetter.getApiDescriptions(
                targetDir = targetDir,
                log = MockLog(),
                packages = arrayOf("tech.igrant.business"),
                classLoader = Thread.currentThread().contextClassLoader
        )
        val sortedExpected = expected.sortedBy { desc -> desc.toString() }
        val sortedActual = actual.sortedBy { desc -> desc.toString() }
        sortedExpected.forEachIndexed { index, desc ->
            Assert.assertEquals(desc, sortedActual[index])
        }
    }

}