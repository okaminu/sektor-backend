package lt.tlistas.loginn.backend

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.WriteConcern
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.beans
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.reactive.function.server.RouterFunctions
import java.util.*

fun beans() = beans {
	bean<ProjectHandler>()
	bean<Routes>()

	bean("mongoTemplate") {
		val mongoClient = MongoClient(ServerAddress(), object : ArrayList<MongoCredential>() {
			init {
				add(MongoCredential.createCredential(System.getenv("MONGODB_USERNAME"),
                        System.getenv("MONGODB_AUTH_DATABASE"),
                        System.getenv("MONGODB_PASSWORD").toCharArray()))
			}
		})
		val mongoTemplate = MongoTemplate(SimpleMongoDbFactory(mongoClient, System.getenv("MONGODB_DATABASE")))
		mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED)
		mongoTemplate
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

	profile("cors") {
		bean("corsFilter") {
			CorsWebFilter { CorsConfiguration().applyPermitDefaultValues() }
		}
	}
}
