package com.azharkova.kmm_concurrency_sample

interface INetworkClient {
    fun request(completion: (Any?)->Unit)
}

expect  class  NetworkClient : INetworkClient {
    override fun request(completion: (Any?)->Unit)
}