package lt.boldadmin.sektor.backend.aspect

import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.sektor.backend.exception.IncorrectConfirmationCodeException
import lt.boldadmin.sektor.backend.exception.IncorrectTokenException
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(0)
class IdentityConfirmationAspect(private val identityConfirmation: IdentityConfirmation) {

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.*.*(..)) && args(req)")
    fun tokenExistsAdvice(req: ServerRequest) {
        val header = getHeader(req)

        if (header.isEmpty() || !identityConfirmation.doesTokenExist(header[0]))
            throw IncorrectTokenException
    }


    @Before("execution(* lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler.confirmCode(..)) && args(req)")
    fun confirmationCodeUserExistsAdvice(req: ServerRequest) {
        val code = req.pathVariable("code")

        if (!identityConfirmation.doesUserByCodeExist(code))
            throw IncorrectConfirmationCodeException
    }

    private fun getHeader(req: ServerRequest) = req.headers().header("auth-token")

}