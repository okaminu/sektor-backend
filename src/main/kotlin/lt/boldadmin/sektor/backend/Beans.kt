package lt.boldadmin.sektor.backend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.*
import lt.boldadmin.nexus.api.type.valueobject.Location
import lt.boldadmin.sektor.backend.route.CollaboratorRoutes
import lt.boldadmin.sektor.backend.route.WorkLogRoutes
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.beans
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.reactive.function.server.RouterFunctions
import java.time.Duration
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
            add(
                MongoCredential.createCredential(
                    environment["MONGO_USERNAME"], environment["MONGO_AUTH_DATABASE"],
                    environment["MONGO_PASSWORD"].toCharArray()
                )
            )
        })
    }

    bean("webHandler") {
        RouterFunctions.toWebHandler(
            ref<CollaboratorRoutes>().router().and(ref<WorkLogRoutes>().router())
        )
    }

    bean("messageSource") {
        ReloadableResourceBundleMessageSource().apply {
            setBasename("messages")
            setDefaultEncoding("UTF-8")
        }
    }

    bean("redisConnectionFactory") {
        val environment = ref<Environment>()
        LettuceConnectionFactory(RedisStandaloneConfiguration(environment["REDIS_HOST"]))
    }

    bean("redisCacheConfiguration") {
        RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()
    }

    bean("projectLocationCacheConfiguration") {
        ref<RedisCacheConfiguration>("redisCacheConfiguration").serializeValuesWith(fromSerializer(
            Jackson2JsonRedisSerializer(Location::class.java).apply { setObjectMapper(jacksonObjectMapper()) }))
            .entryTtl(Duration.ofDays(ref<Environment>()["PROJECT_LOCATION_CACHE_TTL"].toLong()))
    }

    bean("cacheManager") {
        RedisCacheManager.builder(ref<LettuceConnectionFactory>()).withInitialCacheConfigurations(
            mapOf("projectLocation" to ref("projectLocationCacheConfiguration"))
        ).build()
    }

    profile("cors") {
        bean("corsFilter") {
            CorsWebFilter { CorsConfiguration().applyPermitDefaultValues() }
        }
    }
}