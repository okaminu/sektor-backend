package lt.tlistas.loginn.backend.aspect

import lt.tlistas.crowbar.api.exception.AuthenticationException
import lt.tlistas.crowbar.service.AuthenticationService
import org.aspectj.lang.annotation.Aspect
import org.springframework.web.reactive.function.server.ServerRequest
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order

@Aspect
@Order(0)
class CollaboratorHandlerAspect(private val authenticationService: AuthenticationService) {

    @Before("execution(* lt.tlistas.loginn.backend.handler.CollaboratorHandler.*(..))&& args(req)")
    fun authenticate(req: ServerRequest) {
        if (!authenticationService.tokenExists(req.headers().header("auth-token")[0]))
            throw AuthenticationException()
    }
}