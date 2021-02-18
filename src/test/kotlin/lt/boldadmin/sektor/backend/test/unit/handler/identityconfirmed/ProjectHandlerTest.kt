package lt.boldadmin.sektor.backend.test.unit.handler.identityconfirmed

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import lt.boldadmin.crowbar.IdentityConfirmation
import lt.boldadmin.nexus.api.service.UserService
import lt.boldadmin.nexus.api.service.collaborator.CollaboratorService
import lt.boldadmin.nexus.api.type.entity.Project
import lt.boldadmin.nexus.api.type.entity.User
import lt.boldadmin.sektor.backend.handler.identityconfirmed.ProjectHandler
import lt.boldadmin.sektor.backend.route.Routes
import lt.boldadmin.sektor.backend.service.CollaboratorAuthenticationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(MockitoExtension::class)
class ProjectHandlerTest {

    @Mock
    private lateinit var collaboratorServiceStub: CollaboratorService

    @Mock
    private lateinit var userServiceStub: UserService

    @Mock
    private lateinit var identityConfirmationStub: IdentityConfirmation

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        val collaboratorAuthService = CollaboratorAuthenticationService(
            collaboratorServiceStub,
            identityConfirmationStub
        )

        val projectHandler = ProjectHandler(collaboratorAuthService, userServiceStub)
        val routerFunction = Routes(mock(), mock(), mock(), mock(), projectHandler).router()
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build()
        doReturn(COLLABORATOR_ID).`when`(identityConfirmationStub).getUserIdByToken(AUTH_TOKEN)
    }

    @Test
    fun `Provides projects`() {
        val expectedProjects = mutableSetOf(Project("fancyId"))
        val userWithProjects = User().apply { projects = expectedProjects }
        doReturn(userWithProjects).`when`(userServiceStub).getByCollaboratorId(COLLABORATOR_ID)

        val projectsResponseBody = webTestClient.get()
            .uri("/projects")
            .header("auth-token", AUTH_TOKEN)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(object: ParameterizedTypeReference<Set<Project>>() {})
            .returnResult()

        assertThat(projectsResponseBody.responseBody!!).hasSize(1).allMatch { it.id == expectedProjects.first().id }
    }

    companion object {
        private const val COLLABORATOR_ID = "collaboratorId"
        private const val AUTH_TOKEN = "asda454s6d"
    }
}
