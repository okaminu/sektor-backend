package lt.tlistas.loginn.backend.aspect

import lt.tlistas.crowbar.service.AuthenticationService
import lt.tlistas.loginn.backend.exception.IncorrectTokenException
import org.aspectj.lang.annotation.Aspect
import org.springframework.web.reactive.function.server.ServerRequest
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order

@Aspect
@Order(0)
class CollaboratorHandlerAspect(private val authenticationService: AuthenticationService) {

    @Before("execution(* lt.tlistas.loginn.backend.handler.CollaboratorHandler.*(..))&& args(req)")
    fun authenticate(req: ServerRequest) {
        val header = getHeader(req)

        if (header.isEmpty() || !authenticationService.tokenExists(header[0]))
            throw IncorrectTokenException()
    }

    private fun getHeader(req: ServerRequest) = req.headers().header("auth-token")

}