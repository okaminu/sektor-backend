package lt.tlistas.loginn.backend

import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import lt.tlistas.core.api.exception.*
import lt.tlistas.loginn.backend.exception.CollaboratorNotFoundException
import lt.tlistas.mobile.number.confirmation.api.exception.AuthenticationException
import lt.tlistas.mobile.number.confirmation.api.exception.ConfirmationCodeNotFoundException
import lt.tlistas.mobile.number.confirmation.api.exception.ConfirmationMessageGatewayException
import lt.tlistas.mobile.number.confirmation.api.exception.InvalidAddressException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import java.util.logging.Level

class ExceptionHandler : WebExceptionHandler {
    override fun handle(exchange: ServerWebExchange?, ex: Throwable?): Mono<Void> {
        when (ex!!) {
            is GeocodeGatewayException -> {
                LOGGER.log(Level.WARNING, ex.message)
                exchange!!.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            }
            is LocationNotFoundException -> {
                LOGGER.log(Level.WARNING, ex.message)
                exchange!!.response.statusCode = HttpStatus.UNPROCESSABLE_ENTITY
            }
            is ConfirmationMessageGatewayException -> {
                LOGGER.log(Level.WARNING, ex.message)
                exchange!!.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
            }
            is InvalidAddressException -> {
                LOGGER.log(Level.WARNING, ex.message)
                exchange!!.response.statusCode = HttpStatus.UNPROCESSABLE_ENTITY
            }
            is ConfirmationCodeNotFoundException -> {
                exchange!!.response.statusCode = HttpStatus.NOT_FOUND
            }
            is AuthenticationException -> {
                exchange!!.response.statusCode = HttpStatus.UNAUTHORIZED
            }
            is CollaboratorNotFoundException -> {
                exchange!!.response.statusCode = HttpStatus.NOT_FOUND
            }

        }
        return Mono.empty()
    }
}