package com.azharkova.kmm_concurrency_sample

actual class HttpClient : IHttpClient {
    val httpEngine = HttpEngine()

    actual override fun request(request: Request, completion: (Response) -> Unit) {

        httpEngine.request(request) {
            completion(it)
        }
    }
}



