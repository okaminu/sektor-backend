package lt.boldadmin.sektor.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import lt.boldadmin.nexus.api.type.entity.Worklog
import lt.boldadmin.sektor.backend.config.serializer.WorkLogSerializer
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
class WebConfigurer : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(createObjectMapper()))
    }

    private fun createObjectMapper(): ObjectMapper =
        Jackson2ObjectMapperBuilder
            .json()
            .serializerByType(Worklog::class.java, WorkLogSerializer())
            .build()
}
