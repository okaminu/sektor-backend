package lt.tlistas.loginn.backend

import lt.tlistas.loginn.backend.beans.beans
import lt.tlistas.loginn.backend.beans.exceptionHandlerBeans
import lt.tlistas.loginn.backend.exception.handler.*
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.server.WebExceptionHandler
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.ipc.netty.http.server.HttpServer


fun main(args: Array<String>) {

    val context = GenericApplicationContext()
    beans().initialize(context)
    exceptionHandlerBeans().initialize(context)
    XmlBeanDefinitionReader(context).loadBeanDefinitions("classpath:context/context.xml")
    context.refresh()

    val httpHandler = WebHttpHandlerBuilder
            .applicationContext(context)
            .exceptionHandler(*getExceptionHandlers(context))
            .apply { if (context.containsBean("corsFilter")) filter(context.getBean<CorsWebFilter>()) }
            .build()

    HttpServer.create(8090).startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
}

private fun getExceptionHandlers(context: GenericApplicationContext) =
        arrayOf<WebExceptionHandler>(
                context.getBean<CollaboratorNotFoundExceptionHandler>(),
                context.getBean<ConfirmationCodeNotFoundExceptionHandler>(),
                context.getBean<GeocodeGatewayExceptionHandler>(),
                context.getBean<IncorrectTokenExceptionHandler>(),
                context.getBean<InternalErrorExceptionHandler>(),
                context.getBean<InvalidParameterValueExceptionHandler>(),
                context.getBean<LocationNotFoundExceptionHandler>()
        )