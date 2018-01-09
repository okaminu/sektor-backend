package lt.tlistas.loginn.backend

import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer

fun main(args: Array<String>) {
    var server = HttpServer.create(8080)
    var nettyContext = server.start(ReactorHttpHandlerAdapter(
            WebHttpHandlerBuilder.applicationContext(GenericApplicationContext().apply {
                refresh()
            }).build()))

}