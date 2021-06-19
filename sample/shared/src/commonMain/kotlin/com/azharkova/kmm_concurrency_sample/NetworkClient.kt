package com.azharkova.kmm_concurrency_sample

interface INetworkClient {
    fun request(completion: (String)->Unit)
}

expect  class  NetworkClient : INetworkClient {
    override fun request(completion: (String)->Unit)
}