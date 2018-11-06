package lt.boldadmin.sektor.backend.aspect

import lt.boldadmin.nexus.api.service.worklog.WorklogAuthService
import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.web.reactive.function.server.ServerRequest

@Aspect
@Order(1)
class CollaboratorAuthorizationAspect(
    private val collaboratorAuthService: CollaboratorAuthenticationService,
    private val workLogAuthService: WorklogAuthService
) {

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler.*ByIntervalId(..))" +
        " && args(req)")
    fun collaboratorHasWorkLogIntervalAdvice(req: ServerRequest) {
        val collaboratorId = collaboratorAuthService.getCollaboratorId(req)
        if (!workLogAuthService.doesCollaboratorHaveWorkLogInterval(collaboratorId, req.pathVariable("intervalId")))
            throw WorkLogIntervalDoesNotBelongToCollaboratorException()
    }

    @Before("execution(* lt.boldadmin.sektor.backend.handler.identityconfirmed.WorkLogHandler.*ByIntervalIds(..))" +
        " && args(req)")
    fun collaboratorHasWorkLogIntervalsAdvice(req: ServerRequest) {
        val collaboratorId = collaboratorAuthService.getCollaboratorId(req)
        if (!workLogAuthService.doesCollaboratorHaveWorkLogIntervals(collaboratorId, req.pathVariable("intervalIds").split(",")))
            throw WorkLogIntervalDoesNotBelongToCollaboratorException()
    }
}