package lt.tlistas.loginn.backend.exception.handler

import lt.tlistas.core.api.exception.GeocodeGatewayException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class GeocodeGatewayExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is GeocodeGatewayException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
    }
}