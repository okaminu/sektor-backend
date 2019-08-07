package lt.boldadmin.sektor.backend.test.unit.aspect

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import lt.boldadmin.nexus.api.exception.CollaboratorNotFoundException
import lt.boldadmin.nexus.api.service.CollaboratorService
import lt.boldadmin.sektor.backend.aspect.CollaboratorExistenceAspect
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.server.ServerRequest

@ExtendWith(MockitoExtension::class)
class CollaboratorExistenceTest {

    @Mock
    private lateinit var collaboratorServiceSpy: CollaboratorService

    @Mock
    private lateinit var collaboratorAuthServiceStub: CollaboratorAuthenticationService

    @Mock
    private lateinit var serverRequestStub: ServerRequest

    private lateinit var collaboratorAspect: CollaboratorExistenceAspect

    @BeforeEach
    fun `Set up`() {
        collaboratorAspect = CollaboratorExistenceAspect(
            collaboratorServiceSpy, collaboratorAuthServiceStub
        )
    }

    @Test
    fun `Checks if collaborator exists by mobile number`() {
        doReturn(MOBILE_NUMBER).`when`(serverRequestStub).pathVariable("mobileNumber")
        doReturn(true).`when`(collaboratorServiceSpy).existsByMobileNumber(MOBILE_NUMBER)

        collaboratorAspect.collaboratorExistsByMobileNumberAdvice(serverRequestStub)

        verify(collaboratorServiceSpy).existsByMobileNumber(MOBILE_NUMBER)
    }

    @Test
    fun `Throws exception if collaborator is not found by mobile number`() {
        doReturn(MOBILE_NUMBER).`when`(serverRequestStub).pathVariable("mobileNumber")
        doReturn(false).`when`(collaboratorServiceSpy).existsByMobileNumber(MOBILE_NUMBER)

        assertThrows(CollaboratorNotFoundException::class.java) {
            collaboratorAspect.collaboratorExistsByMobileNumberAdvice(serverRequestStub)
        }
    }

    @Test
    fun `Checks if collaborator exists by id`() {
        mockHeaderResponse()
        doReturn(true).`when`(collaboratorServiceSpy).existsById(COLLABORATOR_ID)

        collaboratorAspect.collaboratorExistsByIdAdvice(serverRequestStub)

        verify(collaboratorServiceSpy).existsById(COLLABORATOR_ID)
    }

    @Test
    fun `Throws exception if collaborator is not found by id`() {
        mockHeaderResponse()
        doReturn(false).`when`(collaboratorServiceSpy).existsById(COLLABORATOR_ID)

        assertThrows(CollaboratorNotFoundException::class.java) {
            collaboratorAspect.collaboratorExistsByIdAdvice(serverRequestStub)
        }
    }

    private fun mockHeaderResponse() {
        doReturn(COLLABORATOR_ID).`when`(collaboratorAuthServiceStub).getCollaboratorId(serverRequestStub)
    }

    companion object {
        private val COLLABORATOR_ID = "4a4s65fa4s65df46s5"
        private val MOBILE_NUMBER = "+3701234568"
    }
}