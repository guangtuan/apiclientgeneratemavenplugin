package tech.igrant

enum class HttpMethod {
    POST,
    GET,
    PUT,
    DELETE,
    PATCH
}

data class HttpContextDesc(
        private val url: String,
//        private val fixedParams: Map<String, Any>,
//        private val requestBodyClass: Class<*>,
//        private val responseBodyClass: Class<*>,
        private val httpMethod: HttpMethod?
)