package lt.boldadmin.sektor.backend.test.unit.aspect

import com.nhaarman.mockitokotlin2.doReturn
import lt.boldadmin.nexus.api.service.worklog.WorklogAuthService
import lt.boldadmin.sektor.backend.aspect.CollaboratorAuthorizationAspect
import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.server.ServerRequest

@ExtendWith(MockitoExtension::class)
class CollaboratorAuthorizationAspectTest {

    @Mock
    private lateinit var workLogAuthServiceStub: WorklogAuthService

    @Mock
    private lateinit var collaboratorAuthServiceStub: CollaboratorAuthenticationService

    @Mock
    private lateinit var serverRequestStub: ServerRequest

    private lateinit var aspect: CollaboratorAuthorizationAspect

    @BeforeEach
    fun setUp() {
        aspect = CollaboratorAuthorizationAspect(collaboratorAuthServiceStub, workLogAuthServiceStub)

        doReturn(COLLABORATOR_ID).`when`(collaboratorAuthServiceStub).getCollaboratorId(serverRequestStub)
    }

    @Test
    fun `Throws exception when work log does not belong to collaborator`() {
        val intervalId = "123123"
        doReturn(intervalId).`when`(serverRequestStub).pathVariable("intervalId")
        doReturn(false).`when`(workLogAuthServiceStub).doesCollaboratorHaveWorkLogInterval(COLLABORATOR_ID, intervalId)

        assertThrows(WorkLogIntervalDoesNotBelongToCollaboratorException::class.java) {
            aspect.collaboratorHasWorkLogIntervalAdvice(serverRequestStub)
        }
    }

    @Test
    fun `Does not throw exception when work log belongs to collaborator`() {
        val intervalId = "123123"
        doReturn(intervalId).`when`(serverRequestStub).pathVariable("intervalId")
        doReturn(true).`when`(workLogAuthServiceStub).doesCollaboratorHaveWorkLogInterval(COLLABORATOR_ID, intervalId)

        aspect.collaboratorHasWorkLogIntervalAdvice(serverRequestStub)
    }

    @Test
    fun `Throws exception when one of the work logs don't belong to collaborator`() {
        val intervalIds = listOf("11", "22")
        val intervalIdsString = "${intervalIds[0]},${intervalIds[1]}"
        doReturn(intervalIdsString).`when`(serverRequestStub).pathVariable("intervalIds")
        doReturn(false)
            .`when`(workLogAuthServiceStub).doesCollaboratorHaveWorkLogIntervals(COLLABORATOR_ID, intervalIds)

        assertThrows(WorkLogIntervalDoesNotBelongToCollaboratorException::class.java) {
            aspect.collaboratorHasWorkLogIntervalsAdvice(serverRequestStub)
        }
    }

    @Test
    fun `Does not throw exception when work logs belong to collaborator`() {
        val intervalIds = listOf("11", "22")
        val intervalIdsString = "${intervalIds[0]},${intervalIds[1]}"
        doReturn(intervalIdsString).`when`(serverRequestStub).pathVariable("intervalIds")
        doReturn(true).`when`(workLogAuthServiceStub).doesCollaboratorHaveWorkLogIntervals(COLLABORATOR_ID, intervalIds)

        aspect.collaboratorHasWorkLogIntervalsAdvice(serverRequestStub)
    }

    companion object {
        const val COLLABORATOR_ID = "collaboratorId"
    }

}
