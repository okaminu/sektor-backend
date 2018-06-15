package lt.boldadmin.sektor.backend

import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer


fun main(args: Array<String>) {

    val context = GenericApplicationContext()
    beans().initialize(context)
    XmlBeanDefinitionReader(context).loadBeanDefinitions("classpath:context/context.xml")
    context.refresh()

    val httpHandler = getWebHttpHandler(context)

    HttpServer.create(8090).startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
}

private fun getWebHttpHandler(context: GenericApplicationContext) = WebHttpHandlerBuilder
        .applicationContext(context)
        .apply { if (context.containsBean("corsFilter")) filter(context.getBean<CorsWebFilter>()) }
        .build()