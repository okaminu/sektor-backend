package lt.tlistas.loginn.backend

import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer


fun main(args: Array<String>) {

    val context = GenericApplicationContext().apply {
        beans().initialize(this)
        refresh()
    }

    val httpHandler = WebHttpHandlerBuilder
            .applicationContext(context)
            .apply { if (context.containsBean("corsFilter")) filter(context.getBean<CorsWebFilter>()) }
            .build()

    HttpServer.create(8080).startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
}