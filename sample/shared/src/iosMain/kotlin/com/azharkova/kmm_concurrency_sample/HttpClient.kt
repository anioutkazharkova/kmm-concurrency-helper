package com.azharkova.kmm_concurrency_sample

import com.azharkova.kmm_concurrency_sample.deferred.HttpDefferedEngine
import platform.Foundation.NSLog

actual class HttpClient : IHttpClient {
    val httpEngine = HttpEngine()

    actual override fun request(request: Request, completion: (Response) -> Unit) {

        httpEngine.request(request) {
            completion(it)
        }
    }

    actual override suspend fun request(request: Request):Response {

       val response =  HttpDefferedEngine().request(request)
        //NSLog("response %s",response.content)
return response
        /*val detachedObject = SharedDetachedObject { mutableListOf("a", "b")}

        repeat(50_000){rcount ->
            detachedObject.access {
                val element = "row $rcount"
                NSLog("%s",element)
                it.add(element)
                element
            }
        }*/
    }
}



