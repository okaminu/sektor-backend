package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.service.LocationLoggingService
import lt.tlistas.core.service.UserService
import lt.tlistas.core.type.entity.Collaborator
import lt.tlistas.core.type.entity.Company
import lt.tlistas.core.type.entity.User
import lt.tlistas.core.type.value_object.TimeRange
import lt.tlistas.loginn.backend.CollaboratorHandler
import org.junit.Ignore
import org.junit.Test


class CollaboratorHandlerTest {
	
	@Test
	@Ignore
	fun `Takes collaborator worktime`() {
		val userServiceMock : UserService = mock()
		val locationLoggingService : LocationLoggingService= mock()

		val timeRange = TimeRange(0, 1)
		val user = User().apply {
			company = Company().apply {
				addCollaborator(Collaborator()
						.apply { workTime = timeRange })
			}
		}
		doReturn(user).`when`(userServiceMock).getByEmail(any())

		val collaboratorHandler = CollaboratorHandler(userServiceMock, locationLoggingService)
		val response = collaboratorHandler.getWorkTime(mock())

		verify(userServiceMock).getByEmail(any())
	}
}
