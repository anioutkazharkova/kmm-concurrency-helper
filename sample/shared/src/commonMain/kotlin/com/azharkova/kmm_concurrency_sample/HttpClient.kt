package com.azharkova.kmm_concurrency_sample

interface IHttpClient {
    fun request(request: Request, completion: (Response)->Unit)
}

expect  class  HttpClient : IHttpClient {
    override fun request(request: Request, completion: (Response)->Unit)
}