package lt.tlistas.loginn.backend.exception.handler

import com.amazonaws.services.sns.model.InvalidParameterException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange

class InvalidParameterValueExceptionHandler : TemplateExceptionHandler() {

    override fun canHandle(ex: Throwable) = ex is InvalidParameterException

    override fun handleException(exchange: ServerWebExchange, ex: Throwable) {
        exchange.response.statusCode = HttpStatus.UNPROCESSABLE_ENTITY
    }
}