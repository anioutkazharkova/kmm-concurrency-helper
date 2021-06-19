package com.azharkova.kmm_concurrency_sample

import platform.Foundation.*
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.Future
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze


class ResponseReader: NSObject(), NSURLSessionDataDelegateProtocol {
    var parent:Parent? = null
    override fun URLSession(
        session: NSURLSession,
        dataTask: NSURLSessionDataTask,
        didReceiveData: NSData
    ) {
       NSLog("%f", didReceiveData.length)
       val json = NSString.create(didReceiveData, NSUTF8StringEncoding)
        NSLog("Answer: %@",json)
        parent?.callback(json)
    }

    override fun URLSession(
        session: NSURLSession,
        task: NSURLSessionTask,
        didCompleteWithError: NSError?
    ) {
       NSLog("Error %@",didCompleteWithError)
    }
}

interface Parent {
    fun callback(callback:Any?)
}

    actual class NetworkClient : INetworkClient, Parent {
        @ThreadLocal
        companion object {
            val instance = NetworkClient()
        }
        val responseReader = ResponseReader()
        val urlSession =
            NSURLSession.sessionWithConfiguration(NSURLSessionConfiguration.defaultSessionConfiguration, responseReader.apply { parent = NetworkClient.instance}.freeze(),
                delegateQueue = NSOperationQueue.currentQueue())

        override fun callback(callback: Any?) {
            dispatch_async(dispatch_get_main_queue()) {
                NSLog("requst main: %@", callback)
            }
        }

    var task: NSURLSessionDataTask? = null
    private val worker: Worker = Worker.start()

    actual override fun request(completion: (String)->Unit) {
        background({
            doRequest()
        }, completion)
//}
//}) {

//}
    }


    private fun doRequest() {
        var urlRequest =
            NSMutableURLRequest(NSURL.URLWithString("https://newsapi.org/v2/top-headlines?language=en")!!)
        urlRequest.setAllHTTPHeaderFields(mapOf("X-Api-Key" to "5b86b7593caa4f009fea285cc74129e2"))

       val task = urlSession.freeze().dataTaskWithRequest(urlRequest)/* { data, error, response ->
        data?.let {
            val json = NSString.create(it, NSUTF8StringEncoding)
            NSLog("%s", json)

        }
    }*/
        task?.resume()
    }
}

fun background(block: () -> Unit,completion: (String)->Unit) {
    val future = worker.execute(TransferMode.SAFE, { block.freeze() }) {
        it()
    }
    collectFutures.add(future)
  /*  future.consume {
         dispatch_async(dispatch_get_main_queue()) {
             completion?.invoke("test")
         }
    }*/
}

private val worker = Worker.start()
private val collectFutures = mutableListOf<Future<*>>()