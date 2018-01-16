package lt.tlistas.loginn.backend

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class Routes(private val collaboratorHandler: CollaboratorHandler) {
	
	fun router() = router {
		"/collaborator".nest {
			accept(APPLICATION_JSON).nest {
				GET("/workTime", collaboratorHandler::getWorkTime)
				POST("/logWorkByLocation", collaboratorHandler::logWorkByLocation)
			}

		}
	}
}