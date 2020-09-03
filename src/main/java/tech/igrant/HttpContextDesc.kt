package tech.igrant

enum class HttpMethod {
    POST,
    GET,
    PUT,
    DELETE,
    PATCH
}

data class ParamsDesc(
        private val name: String,
        private val paramsClass: String,
        private val require: Boolean,
        private val asQueryParamKey: String?
)

data class HttpContextDesc(
        private val url: String,
//        private val fixedParams: Map<String, Any>,
        private val responseBodyClass: String,
        private val listOfParams: List<ParamsDesc>,
        private val httpMethod: HttpMethod?
)