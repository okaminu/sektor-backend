package lt.tlistas.loginn.backend

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router

class Routes(private val collaboratorHandler: CollaboratorHandler) {
	
	fun router() = router {
		"/collaborator".nest {
			accept(APPLICATION_JSON).nest {
				GET("/workTime", collaboratorHandler::getWorkTime)
				POST("/logWorkByLocation", collaboratorHandler::logWorkByLocation)
			}
		}
        GET("/status", { ok().body(fromObject("OK")) })
    }
}