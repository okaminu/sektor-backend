package lt.tlistas.loginn.backend

import lt.tlistas.core.exception.LocationByAddressNotFoundException
import lt.tlistas.core.exception.LocationGatewayException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

class ExceptionHandler : WebExceptionHandler {
    override fun handle(exchange: ServerWebExchange?, ex: Throwable?): Mono<Void> {
        when (ex!!) {
            is LocationGatewayException -> exchange!!.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            is LocationByAddressNotFoundException -> exchange!!.response.statusCode = HttpStatus.UNPROCESSABLE_ENTITY
        }
        return Mono.empty()
    }
}