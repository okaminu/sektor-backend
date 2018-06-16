package lt.boldadmin.sektor.backend.exception.handler

import lt.boldadmin.sektor.backend.exception.IncorrectConfirmationCodeException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class IncorrectConfirmationCodeExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is IncorrectConfirmationCodeException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
    }
}