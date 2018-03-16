package lt.tlistas.loginn.backend.exception.handler

import com.amazonaws.services.sns.model.InternalErrorException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class InternalErrorExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is InternalErrorException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
    }
}