package lt.tlistas.loginn.backend

import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.ipc.netty.http.server.HttpServer


fun main(args: Array<String>) {
    var server = HttpServer.create(8080)

    val helloWorld = HandlerFunction<ServerResponse> { ServerResponse.ok().body(fromObject("Hello World")) }
    val helloWorldRoute = RouterFunctions.route(RequestPredicates.path("/hello-world"), helloWorld)
    RouterFunctions.toHttpHandler(helloWorldRoute)
    server.startAndAwait(ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(helloWorldRoute)))
}