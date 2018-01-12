package lt.tlistas.loginn.backend

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class Routes(private val projectHandler: ProjectHandler) {
	
	fun router() = router {
		"/api".nest {
			accept(APPLICATION_JSON).nest {
				GET("/projects", projectHandler::findAll)
				GET("/save", projectHandler::save)
			}

		}
	}
}