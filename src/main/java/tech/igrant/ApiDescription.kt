package tech.igrant

enum class HttpMethod {
    POST,
    GET,
    PUT,
    DELETE,
    PATCH
}

enum class ParamType {
    BODY,
    PATH,
    QUERY,
    UN_KNOWN
}

data class ParamsDesc(
        private val value: Any? = null,
        private val name: String,
        private val paramsClass: String,
        private val require: Boolean,
        private val paramType: ParamType
)

data class ApiDescription(
        private val name: String,
        private val url: String,
//        private val fixedParams: Map<String, Any>,
        private val responseBodyClass: String,
        private val listOfParams: List<ParamsDesc>,
        private val httpMethod: HttpMethod?
)