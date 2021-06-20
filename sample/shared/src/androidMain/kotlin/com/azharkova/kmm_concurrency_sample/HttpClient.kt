package com.azharkova.kmm_concurrency_sample

actual class HttpClient : IHttpClient {
    actual override fun request(request: Request, completion: (Response)->Unit) {
        TODO("Not yet implemented")
    }
}