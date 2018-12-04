package lt.boldadmin.sektor.backend.exception.handler

import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class WorkLogDoesNotBelongExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is WorkLogIntervalDoesNotBelongToCollaboratorException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
    }
}