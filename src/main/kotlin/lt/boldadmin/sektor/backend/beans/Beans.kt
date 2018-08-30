package lt.boldadmin.sektor.backend.beans

import com.mongodb.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import java.util.*

@Configuration
class Beans {

    @Bean("mongoTemplate")
    fun mongoTemplate(mongoClient: MongoClient, environment: Environment) =
        MongoTemplate(SimpleMongoDbFactory(mongoClient, environment["MONGO_DATABASE"])).apply {
            setWriteConcern(WriteConcern.ACKNOWLEDGED)
        }

    @Bean("mongoClient")
    fun mongoClient(environment: Environment) =
        MongoClient(ServerAddress(environment["MONGO_HOST"]), ArrayList<MongoCredential>().apply {
            add(
                MongoCredential.createCredential(
                    environment["MONGO_USERNAME"], environment["MONGO_AUTH_DATABASE"],
                    environment["MONGO_PASSWORD"].toCharArray()
                )
            )
        })

    @Bean("messageSource")
    fun messageSource() =
        ReloadableResourceBundleMessageSource().apply {
        setBasename("messages")
        setDefaultEncoding("UTF-8")
    }


    @Bean("corsFilter")
    @Profile("cors")
    fun corsFilter() =
        CorsWebFilter { CorsConfiguration().applyPermitDefaultValues() }

}