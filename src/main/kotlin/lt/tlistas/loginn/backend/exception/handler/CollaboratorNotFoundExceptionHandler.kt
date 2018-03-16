package lt.tlistas.loginn.backend.exception.handler

import lt.tlistas.loginn.backend.exception.CollaboratorNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class CollaboratorNotFoundExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is CollaboratorNotFoundException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.NOT_FOUND
    }
}