package lt.boldadmin.sektor.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.sektor.backend.aspect.CollaboratorAuthorizationAspect
import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.reactive.function.server.ServerRequest

@RunWith(MockitoJUnitRunner::class)
class CollaboratorAuthorizationAspectTest {

    @Mock
    private lateinit var workLogServiceStub: WorkLogService

    @Mock
    private lateinit var collaboratorAuthServiceStub: CollaboratorAuthenticationService

    @Mock
    private lateinit var serverRequestStub: ServerRequest

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    @Test
    fun `throws exception when worklog does not belong to collaborator`() {
        val intervalId = "123123"
        val collaboratorId = "collaboratorId"
        doReturn(intervalId).`when`(serverRequestStub).pathVariable("intervalId")
        doReturn(collaboratorId).`when`(collaboratorAuthServiceStub).getCollaboratorId(serverRequestStub)
        doReturn(false).`when`(workLogServiceStub).doesCollaboratorHaveWorkLogInterval(collaboratorId, intervalId)

        expectedException.expect(WorkLogIntervalDoesNotBelongToCollaboratorException::class.java)

        CollaboratorAuthorizationAspect(workLogServiceStub, collaboratorAuthServiceStub)
            .collaboratorHasWorkLogIntervalAdvice(serverRequestStub)
    }

    @Test
    fun `does not throw exception when worklog does belongs to collaborator`() {
        val intervalId = "123123"
        val collaboratorId = "collaboratorId"
        doReturn(intervalId).`when`(serverRequestStub).pathVariable("intervalId")
        doReturn(collaboratorId).`when`(collaboratorAuthServiceStub).getCollaboratorId(serverRequestStub)
        doReturn(true).`when`(workLogServiceStub).doesCollaboratorHaveWorkLogInterval(collaboratorId, intervalId)

        CollaboratorAuthorizationAspect(workLogServiceStub, collaboratorAuthServiceStub)
            .collaboratorHasWorkLogIntervalAdvice(serverRequestStub)
    }
}