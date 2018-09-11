package lt.boldadmin.sektor.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import lt.boldadmin.nexus.service.worklog.WorkLogService
import lt.boldadmin.sektor.backend.aspect.CollaboratorAuthorizationAspect
import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.Before
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

    @Before
    fun setUp(){
        doReturn(COLLABORATOR_ID).`when`(collaboratorAuthServiceStub).getCollaboratorId(serverRequestStub)
    }

    @Test
    fun `throws exception when work log does not belong to collaborator`() {
        val intervalId = "123123"
        doReturn(intervalId).`when`(serverRequestStub).pathVariable("intervalId")
        doReturn(false).`when`(workLogServiceStub).doesCollaboratorHaveWorkLogInterval(COLLABORATOR_ID, intervalId)

        expectedException.expect(WorkLogIntervalDoesNotBelongToCollaboratorException::class.java)

        CollaboratorAuthorizationAspect(workLogServiceStub, collaboratorAuthServiceStub)
            .collaboratorHasWorkLogIntervalAdvice(serverRequestStub)
    }

    @Test
    fun `does not throw exception when work log does belongs to collaborator`() {
        val intervalId = "123123"
        doReturn(intervalId).`when`(serverRequestStub).pathVariable("intervalId")
        doReturn(true).`when`(workLogServiceStub).doesCollaboratorHaveWorkLogInterval(COLLABORATOR_ID, intervalId)

        CollaboratorAuthorizationAspect(workLogServiceStub, collaboratorAuthServiceStub)
            .collaboratorHasWorkLogIntervalAdvice(serverRequestStub)
    }

    @Test
    fun `throws exception when one of the work logs don't belong to collaborator`() {
        val intervalIds = listOf("11", "22")
        val intervalIdsString = "${intervalIds[0]},${intervalIds[1]}"
        doReturn(intervalIdsString).`when`(serverRequestStub).pathVariable("intervalIds")
        doReturn(false).`when`(workLogServiceStub).doesCollaboratorHaveWorkLogIntervals(COLLABORATOR_ID, intervalIds)

        expectedException.expect(WorkLogIntervalDoesNotBelongToCollaboratorException::class.java)

        CollaboratorAuthorizationAspect(workLogServiceStub, collaboratorAuthServiceStub)
            .collaboratorHasWorkLogIntervalsAdvice(serverRequestStub)
    }

    @Test
    fun `does not throw exception when work logs belong to collaborator`() {
        val intervalIds = listOf("11", "22")
        val intervalIdsString = "${intervalIds[0]},${intervalIds[1]}"
        doReturn(intervalIdsString).`when`(serverRequestStub).pathVariable("intervalIds")
        doReturn(true).`when`(workLogServiceStub).doesCollaboratorHaveWorkLogIntervals(COLLABORATOR_ID, intervalIds)

        CollaboratorAuthorizationAspect(workLogServiceStub, collaboratorAuthServiceStub)
            .collaboratorHasWorkLogIntervalsAdvice(serverRequestStub)
    }

    companion object {
        const val COLLABORATOR_ID = "collaboratorId"
    }

}