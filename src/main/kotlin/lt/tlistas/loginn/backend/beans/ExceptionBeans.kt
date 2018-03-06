package lt.tlistas.loginn.backend.beans

import lt.tlistas.loginn.backend.exception.handler.*
import org.springframework.context.support.beans
import org.springframework.web.server.handler.ExceptionHandlingWebHandler

fun exceptionBeans() = beans {
    bean<CollaboratorNotFoundExceptionHandler>()
    bean<ConfirmationCodeNotFoundExceptionHandler>()
    bean<GeocodeGatewayExceptionHandler>()
    bean<IncorrectTokenExceptionHandler>()
    bean<InternalErrorExceptionHandler>()
    bean<InvalidParameterValueExceptionHandler>()
    bean<LocationNotFoundExceptionHandler>()
    bean<ExceptionHandlingWebHandler>()
}