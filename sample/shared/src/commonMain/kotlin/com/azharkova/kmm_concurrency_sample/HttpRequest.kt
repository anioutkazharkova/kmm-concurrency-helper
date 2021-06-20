package com.azharkova.kmm_concurrency_sample

enum class Method(val value: String) {
    GET("GET"),

    POST("POST"),

    PUT("PUT");
}

data class Request(val url: String, val method: Method, val headers: Map<Any?,String>)

data class Response(val code: Long = 0, var content: String? = null, val error: Error? = null)