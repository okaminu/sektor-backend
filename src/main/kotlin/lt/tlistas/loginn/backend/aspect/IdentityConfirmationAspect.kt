package lt.tlistas.loginn.backend.aspect

import lt.tlistas.crowbar.IdentityConfirmation
import lt.tlistas.loginn.backend.exception.IncorrectConfirmationCodeException
import lt.tlistas.loginn.backend.exception.IncorrectTokenException
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(0)
class IdentityConfirmationAspect(private val identityConfirmation: IdentityConfirmation) {

    @Before("execution(* lt.tlistas.loginn.backend.handler.token.*.*(..)) && args(req)")
    fun tokenExistsAdvise(req: ServerRequest) {
        val header = getHeader(req)

        if (header.isEmpty() || !identityConfirmation.doesTokenExist(header[0]))
            throw IncorrectTokenException()
    }


    @Before("execution(* lt.tlistas.loginn.backend.handler.IdentityConfirmationHandler.confirmCode(..)) && args(req)")
    fun confirmationCodeUserExistsAdvise(req: ServerRequest) {
        val code = req.pathVariable("code")

        if (!identityConfirmation.doesUserByCodeExist(code))
            throw IncorrectConfirmationCodeException()
    }

    private fun getHeader(req: ServerRequest) = req.headers().header("auth-token")

}