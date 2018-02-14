package lt.tlistas.loginn.backend

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.WriteConcern
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.beans
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.reactive.function.server.RouterFunctions
import java.util.*

fun beans() = beans {
	bean<CollaboratorHandler>()
	bean<Routes>()

	bean("mongoTemplate") {
		MongoTemplate(SimpleMongoDbFactory(ref(), ref<Environment>()["MONGO_DATABASE"])).apply {
			setWriteConcern(WriteConcern.ACKNOWLEDGED)
		}
	}

	bean("mongoClient") {
		val environment = ref<Environment>()
		MongoClient(ServerAddress(environment["MONGO_HOST"]), object : ArrayList<MongoCredential>() {
			init {
				add(MongoCredential.createCredential(environment["MONGO_USERNAME"],
						environment["MONGO_AUTH_DATABASE"],
						environment["MONGO_PASSWORD"].toCharArray()))
			}
		})
	}

	bean("webHandler") {
		RouterFunctions.toWebHandler(ref<Routes>().router())
	}
	bean("messageSource") {
		ReloadableResourceBundleMessageSource().apply {
			setBasename("messages")
			setDefaultEncoding("UTF-8")
		}
	}

	bean("exceptionHandler") {
		ExceptionHandler()
	}

	profile("cors") {
		bean("corsFilter") {
			CorsWebFilter { CorsConfiguration().applyPermitDefaultValues() }
		}
	}
}
