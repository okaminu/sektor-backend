package lt.boldadmin.sektor.backend.exception.handler

import lt.boldadmin.sektor.backend.exception.IncorrectTokenException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

object IncorrectTokenExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is IncorrectTokenException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
    }
}
