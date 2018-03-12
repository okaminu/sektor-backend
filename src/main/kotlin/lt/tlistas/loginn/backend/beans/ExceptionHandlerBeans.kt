package lt.tlistas.loginn.backend.beans

import lt.tlistas.loginn.backend.exception.handler.*
import org.springframework.context.support.beans
import org.springframework.web.server.handler.ExceptionHandlingWebHandler

fun exceptionHandlerBeans() = beans {
    bean<CollaboratorNotFoundExceptionHandler>()
    bean<IncorrectConfirmationCodeExceptionHandler>()
    bean<GeocodeGatewayExceptionHandler>()
    bean<IncorrectTokenExceptionHandler>()
    bean<InternalErrorExceptionHandler>()
    bean<InvalidParameterValueExceptionHandler>()
    bean<LocationNotFoundExceptionHandler>()
    bean<ExceptionHandlingWebHandler>()
}