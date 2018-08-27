package lt.boldadmin.sektor.backend.aspect

import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.sektor.backend.exception.CollaboratorNotFoundException
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(1)
class CollaboratorExistenceAspect(
    private val collaboratorService: CollaboratorService,
    private val collaboratorAuthService: CollaboratorAuthenticationService) {

    @Before("execution(* lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler.requestCode(..)) && args(req)")
    fun collaboratorExistsByMobileNumberAdvice(req: ServerRequest) {
        if (!collaboratorService.existsByMobileNumber(req.pathVariable("mobileNumber")))
            throw CollaboratorNotFoundException()
    }

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.*.*(..)) && args(req)")
    fun collaboratorExistsByIdAdvice(req: ServerRequest) {
        if (!collaboratorService.existsById(collaboratorAuthService.getCollaboratorId(req)))
            throw CollaboratorNotFoundException()
    }
}