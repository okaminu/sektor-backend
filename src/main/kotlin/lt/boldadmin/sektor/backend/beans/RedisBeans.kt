package lt.boldadmin.sektor.backend.beans

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lt.boldadmin.nexus.api.type.valueobject.Location
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
open class RedisBeans {

    @Bean("redisConnectionFactory")
    open fun redisConnectionFactory(environment: Environment) =
        LettuceConnectionFactory(RedisStandaloneConfiguration(environment["REDIS_HOST"]))

    @Bean("redisCacheConfiguration")
    open fun redisCacheConfiguration(): RedisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()

    @Bean("projectLocationCacheConfiguration")
    open fun projectLocationCacheConfiguration(
        redisCacheConfiguration: RedisCacheConfiguration, environment: Environment
    ): RedisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
        RedisSerializationContext.SerializationPair.fromSerializer(Jackson2JsonRedisSerializer(Location::class.java)
            .apply {
            setObjectMapper(
                jacksonObjectMapper()
            )
        })
    ).entryTtl(Duration.ofDays(environment["PROJECT_LOCATION_CACHE_TTL"].toLong()))

    @Bean("cacheManager")
    open fun cacheManager(
        lettuceConnectionFactory: LettuceConnectionFactory, projectLocationCacheConfiguration: RedisCacheConfiguration
    ): RedisCacheManager =
        RedisCacheManager
            .builder(lettuceConnectionFactory)
            .withInitialCacheConfigurations(mapOf("projectLocation" to projectLocationCacheConfiguration))
            .build()
}