package lt.tlistas.loginn.backend

import lt.tlistas.loginn.backend.route.CollaboratorRoutes
import lt.tlistas.loginn.backend.route.WorkLogRoutes
import java.util.*

fun beans() = beans {

    bean("mongoTemplate") {
        MongoTemplate(SimpleMongoDbFactory(ref(), ref<Environment>()["MONGO_DATABASE"])).apply {
            setWriteConcern(WriteConcern.ACKNOWLEDGED)
        }
    }

    bean("mongoClient") {
        val environment = ref<Environment>()
        MongoClient(ServerAddress(environment["MONGO_HOST"]), ArrayList<MongoCredential>().apply {
            add(MongoCredential.createCredential(environment["MONGO_USERNAME"],
                    environment["MONGO_AUTH_DATABASE"],
                    environment["MONGO_PASSWORD"].toCharArray()))
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