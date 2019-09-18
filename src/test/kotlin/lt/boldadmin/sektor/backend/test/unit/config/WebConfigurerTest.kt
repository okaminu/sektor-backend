package lt.boldadmin.sektor.backend.test.unit.config

import com.nhaarman.mockitokotlin2.*
import lt.boldadmin.sektor.backend.config.WebConfigurer
import org.junit.jupiter.api.Test
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonEncoder

class WebConfigurerTest {

    @Test
    fun `Replaces default JSON encoder`() {
        val serverCodecConfigurerStub: ServerCodecConfigurer = mock()
        val serverDefaultCodecSpy: ServerCodecConfigurer.ServerDefaultCodecs = mock()

        doReturn(serverDefaultCodecSpy).`when`(serverCodecConfigurerStub).defaultCodecs()

        WebConfigurer().configureHttpMessageCodecs(serverCodecConfigurerStub)

        verify(serverDefaultCodecSpy).jackson2JsonEncoder(any<Jackson2JsonEncoder>())
    }
}
