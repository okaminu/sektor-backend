package lt.boldadmin.sektor.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.nexus.service.CollaboratorService
import lt.boldadmin.sektor.backend.aspect.CollaboratorExistenceAspect
import lt.boldadmin.sektor.backend.exception.CollaboratorNotFoundException
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
class CollaboratorExistenceTest {

    @Mock
    private lateinit var collaboratorServiceSpy: CollaboratorService

    @Mock
    private lateinit var collaboratorAuthServiceStub: CollaboratorAuthenticationService

    @Mock
    private lateinit var serverRequestMock: ServerRequest

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    private lateinit var collaboratorAspect: CollaboratorExistenceAspect

    @Before
    fun `Set up`() {
        collaboratorAspect = CollaboratorExistenceAspect(
            collaboratorServiceSpy, collaboratorAuthServiceStub
        )
    }

    @Test
    fun `Checks if collaborator exists by mobile number`() {
        doReturn(MOBILE_NUMBER).`when`(serverRequestMock).pathVariable("mobileNumber")
        doReturn(true).`when`(collaboratorServiceSpy).existsByMobileNumber(MOBILE_NUMBER)

        collaboratorAspect.collaboratorExistsByMobileNumberAdvice(serverRequestMock)

        verify(collaboratorServiceSpy).existsByMobileNumber(MOBILE_NUMBER)
    }

    @Test
    fun `Throws exception if collaborator is not found by mobile number`() {
        expectedException.expect(CollaboratorNotFoundException::class.java)
        doReturn(MOBILE_NUMBER).`when`(serverRequestMock).pathVariable("mobileNumber")
        doReturn(false).`when`(collaboratorServiceSpy).existsByMobileNumber(MOBILE_NUMBER)

        collaboratorAspect.collaboratorExistsByMobileNumberAdvice(serverRequestMock)
    }

    @Test
    fun `Checks if collaborator exists by id`() {
        mockHeaderResponse()
        doReturn(true).`when`(collaboratorServiceSpy).existsById(COLLABORATOR_ID)

        collaboratorAspect.collaboratorExistsByIdAdvice(serverRequestMock)

        verify(collaboratorServiceSpy).existsById(COLLABORATOR_ID)
    }

    @Test
    fun `Throws exception if collaborator is not found by id`() {
        expectedException.expect(CollaboratorNotFoundException::class.java)
        mockHeaderResponse()
        doReturn(false).`when`(collaboratorServiceSpy).existsById(COLLABORATOR_ID)

        collaboratorAspect.collaboratorExistsByIdAdvice(serverRequestMock)
    }

    private fun mockHeaderResponse() {
        doReturn(COLLABORATOR_ID).`when`(collaboratorAuthServiceStub).getCollaboratorId(serverRequestMock)
    }

    companion object {
        private val COLLABORATOR_ID = "4a4s65fa4s65df46s5"
        private val MOBILE_NUMBER = "+3701234568"
    }
}