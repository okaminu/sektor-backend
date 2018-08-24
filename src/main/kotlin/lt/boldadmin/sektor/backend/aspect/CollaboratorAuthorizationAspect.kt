package lt.boldadmin.sektor.backend.aspect

import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(1)
class CollaboratorAuthorizationAspect(
    private val workLogService: WorkLogService,
    private val collaboratorAuthService: CollaboratorAuthenticationService) {

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler.updateDescription(..))" +
        " && args(req)")
    fun collaboratorHasWorkLogIntervalAdvice(req: ServerRequest) {
        val collaboratorId = collaboratorAuthService.getCollaboratorId(req)
        if (!workLogService.doesCollaboratorHaveWorkLogInterval(collaboratorId, req.pathVariable("intervalId")))
            throw WorkLogIntervalDoesNotBelongToCollaboratorException()
    }
}