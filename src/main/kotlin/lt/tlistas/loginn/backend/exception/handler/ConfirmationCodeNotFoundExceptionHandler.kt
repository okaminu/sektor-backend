package lt.tlistas.loginn.backend.exception.handler

import lt.tlistas.crowbar.exception.ConfirmationCodeNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class ConfirmationCodeNotFoundExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is ConfirmationCodeNotFoundException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.NOT_FOUND
    }
}