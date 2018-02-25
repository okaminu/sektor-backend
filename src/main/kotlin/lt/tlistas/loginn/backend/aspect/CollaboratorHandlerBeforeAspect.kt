package lt.tlistas.loginn.backend.aspect

import lt.tlistas.mobile.number.confirmation.api.exception.AuthenticationException
import lt.tlistas.mobile.number.confirmation.service.AuthenticationService
import org.aspectj.lang.annotation.Aspect
import org.springframework.web.reactive.function.server.ServerRequest
import org.aspectj.lang.annotation.Before

@Aspect
class CollaboratorHandlerBeforeAspect(private val authenticationService: AuthenticationService) {

    @Before("execution(* lt.tlistas.loginn.backend.handler.CollaboratorHandler.*(..))&& args(req)")
    fun authenticate(req: ServerRequest) {
        if (!authenticationService.tokenExists(req.headers().header("auth-token")[0]))
            throw AuthenticationException()
    }
}