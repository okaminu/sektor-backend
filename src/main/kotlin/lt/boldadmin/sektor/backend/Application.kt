package lt.boldadmin.sektor.backend

import lt.boldadmin.sektor.backend.factory.beans
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.netty.http.server.HttpServer

fun main() {
    val context = GenericApplicationContext()
    initializeBeans(context)
    context.refresh()

    val httpHandler = getWebHttpHandler(context)

    HttpServer.create().port(8090).handle(ReactorHttpHandlerAdapter(httpHandler)).bindNow().apply {
        onDispose().block()
    }
}

private fun initializeBeans(context: GenericApplicationContext) {
    beans().initialize(context)
    XmlBeanDefinitionReader(context).loadBeanDefinitions("classpath:context/context.xml")
}

private fun getWebHttpHandler(context: GenericApplicationContext) = WebHttpHandlerBuilder
        .applicationContext(context)
        .apply { if (context.containsBean("corsFilter")) filter(context.getBean<CorsWebFilter>()) }
        .build()
