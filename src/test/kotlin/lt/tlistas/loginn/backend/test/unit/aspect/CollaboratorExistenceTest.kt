package lt.tlistas.loginn.backend.test.unit.aspect

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.CollaboratorService
import lt.tlistas.crowbar.IdentityConfirmation
import lt.tlistas.loginn.backend.aspect.CollaboratorExistenceAspect
import lt.tlistas.loginn.backend.exception.CollaboratorNotFoundException
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
    private lateinit var collaboratorServiceMock: CollaboratorService

    @Mock
    private lateinit var identityConfirmationMock: IdentityConfirmation

    @Mock
    private lateinit var serverRequestMock: ServerRequest

    @Mock
    private lateinit var headersMock: ServerRequest.Headers

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    private lateinit var collaboratorAspect: CollaboratorExistenceAspect

    @Before
    fun `Set up`() {
        collaboratorAspect = CollaboratorExistenceAspect(collaboratorServiceMock, identityConfirmationMock)
    }

    @Test
    fun `Checks if collaborator exists by mobile number`() {
        doReturn(MOBILE_NUMBER).`when`(serverRequestMock).pathVariable("mobileNumber")
        doReturn(true).`when`(collaboratorServiceMock).existsByMobileNumber(MOBILE_NUMBER)

        collaboratorAspect.collaboratorExistsByMobileNumberAdvise(serverRequestMock)

        verify(collaboratorServiceMock).existsByMobileNumber(MOBILE_NUMBER)
    }

    @Test
    fun `Throws exception if collaborator is not found by mobile number`() {
        expectedException.expect(CollaboratorNotFoundException::class.java)
        doReturn(MOBILE_NUMBER).`when`(serverRequestMock).pathVariable("mobileNumber")
        doReturn(false).`when`(collaboratorServiceMock).existsByMobileNumber(MOBILE_NUMBER)

        collaboratorAspect.collaboratorExistsByMobileNumberAdvise(serverRequestMock)
    }

    @Test
    fun `Checks if collaborator exists by id`() {
        mockHeaderResponse()
        doReturn(true).`when`(collaboratorServiceMock).existsById(COLLABORATOR_ID)

        collaboratorAspect.collaboratorExistsByIdAdvise(serverRequestMock)

        verify(collaboratorServiceMock).existsById(COLLABORATOR_ID)
    }

    @Test
    fun `Throws exception if collaborator is not found by id`() {
        expectedException.expect(CollaboratorNotFoundException::class.java)
        mockHeaderResponse()
        doReturn(false).`when`(collaboratorServiceMock).existsById(COLLABORATOR_ID)

        collaboratorAspect.collaboratorExistsByIdAdvise(serverRequestMock)
    }

    private fun mockHeaderResponse() {
        doReturn(headersMock).`when`(serverRequestMock).headers()
        doReturn(HEADER_LIST).`when`(headersMock).header("auth-token")
        doReturn(COLLABORATOR_ID).`when`(identityConfirmationMock).getUserIdByToken(HEADER_LIST[0])
    }

    companion object {
        private val COLLABORATOR_ID = "4a4s65fa4s65df46s5"
        private val MOBILE_NUMBER = "+3701234568"
        private val HEADER_LIST = listOf("saf654as6df48a")
    }
}