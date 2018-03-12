package lt.tlistas.loginn.backend.beans

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.WriteConcern
import lt.tlistas.loginn.backend.aspect.CollaboratorExistenceAspect
import lt.tlistas.loginn.backend.aspect.TokenExistenceAspect
import lt.tlistas.loginn.backend.handler.AuthenticationHandler
import lt.tlistas.loginn.backend.handler.CollaboratorHandler
import lt.tlistas.loginn.backend.handler.WorkLogHandler
import lt.tlistas.loginn.backend.route.CollaboratorRoutes
import lt.tlistas.loginn.backend.route.WorkLogRoutes
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
    bean<WorkLogHandler>()
    bean<AuthenticationHandler>()

    bean<TokenExistenceAspect>()
    bean<CollaboratorExistenceAspect>()

    bean<CollaboratorRoutes>()
    bean<WorkLogRoutes>()

    bean("mongoTemplate") {
        MongoTemplate(SimpleMongoDbFactory(ref(), ref<Environment>()["MONGO_DATABASE"])).apply {
            setWriteConcern(WriteConcern.ACKNOWLEDGED)
        }
    }

    bean("mongoClient") {
        val environment = ref<Environment>()
        MongoClient(ServerAddress(), object : ArrayList<MongoCredential>() {
            init {
                add(MongoCredential.createCredential(environment["MONGO_USERNAME"],
                        environment["MONGO_AUTH_DATABASE"],
                        environment["MONGO_PASSWORD"].toCharArray()))
            }
        })
    }

    bean("webHandler") {
        RouterFunctions.toWebHandler(ref<CollaboratorRoutes>().router()
                .and(ref<WorkLogRoutes>().router()))
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
