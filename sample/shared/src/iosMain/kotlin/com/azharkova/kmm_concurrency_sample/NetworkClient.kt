package com.azharkova.kmm_concurrency_sample

import platform.Foundation.*
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.Future
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze


class ResponseReader: NSObject(), NSURLSessionDataDelegateProtocol {
    var completion: ((Any?)->Unit)? = null
    override fun URLSession(
        session: NSURLSession,
        dataTask: NSURLSessionDataTask,
        didReceiveData: NSData
    ) {
       NSLog("%f", didReceiveData.length)
       val json = NSString.create(didReceiveData, NSUTF8StringEncoding)
        NSLog("Answer: %@",json)
        main { completion?.invoke(json) }
        /*dispatch_async(dispatch_get_main_queue()) {
            completion?.invoke(json)
        }*/
    }

    override fun URLSession(
        session: NSURLSession,
        task: NSURLSessionTask,
        didCompleteWithError: NSError?
    ) {
       NSLog("Error %@",didCompleteWithError)
    }
}

    actual class NetworkClient : INetworkClient {

    actual override fun request(completion: (Any?)->Unit) {
        val responseReader = ResponseReader().apply { this.completion = completion
        }
        val urlSession =
            NSURLSession.sessionWithConfiguration(
                NSURLSessionConfiguration.defaultSessionConfiguration, responseReader.freeze(),
                delegateQueue = NSOperationQueue.currentQueue()
            )
        val urlRequest =
            NSMutableURLRequest(NSURL.URLWithString("https://newsapi.org/v2/top-headlines?language=en")!!).apply {
                setAllHTTPHeaderFields(mapOf("X-Api-Key" to "5b86b7593caa4f009fea285cc74129e2"))
            }

        fun doRequest() {
            val task = urlSession.freeze().dataTaskWithRequest(urlRequest)
            task?.resume()
        }

        background({
            doRequest()
        })
    }
}

fun background(block: () -> Unit) {
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

fun main(block:()->Unit) {
    block.freeze().apply {
        val freezedBlock = this
        dispatch_async(dispatch_get_main_queue()) {
            freezedBlock()
        }
    }
}

private val worker = Worker.start()
private val collectFutures = mutableListOf<Future<*>>()