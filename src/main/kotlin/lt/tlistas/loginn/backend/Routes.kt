package lt.tlistas.loginn.backend

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

class Routes(private val userHandler: UserHandler) {
	
	fun router() = router {
		"/api".nest {
			accept(APPLICATION_JSON).nest {
				GET("/users", userHandler::findAll)
			}

		}
		resources("/**", ClassPathResource("static/"))
	}
}