package lt.boldadmin.sektor.backend.aspect

import lt.boldadmin.nexus.api.exception.CollaboratorNotFoundException
import lt.boldadmin.nexus.api.service.collaborator.CollaboratorService
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(1)
class CollaboratorExistenceAspect(
    private val collaboratorService: CollaboratorService,
    private val collaboratorAuthService: CollaboratorAuthenticationService
) {

    @Before("execution(* lt.boldadmin.sektor.backend.handler.IdentityConfirmationHandler.requestCode(..)) && args(req)")
    fun collaboratorExistsByMobileNumberAdvice(req: ServerRequest) {
        val mobileNumber = req.pathVariable("mobileNumber")
        if (!collaboratorService.existsByMobileNumber(mobileNumber))
            throw CollaboratorNotFoundException("Collaborator not found by mobile number $mobileNumber")
    }

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.*.*(..)) && args(req)")
    fun collaboratorExistsByIdAdvice(req: ServerRequest) {
        val collaboratorId = collaboratorAuthService.getCollaboratorId(req)
        if (!collaboratorService.existsById(collaboratorId))
            throw CollaboratorNotFoundException("Collaborator not found my id $collaboratorId")
    }
}
