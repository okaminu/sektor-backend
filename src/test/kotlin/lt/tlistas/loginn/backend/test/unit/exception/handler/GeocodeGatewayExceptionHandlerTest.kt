package lt.tlistas.loginn.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.api.exception.GeocodeGatewayException
import lt.tlistas.loginn.backend.exception.handler.GeocodeGatewayExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class GeocodeGatewayExceptionHandlerTest {

    private lateinit var geocodeGatewayExceptionHandler: GeocodeGatewayExceptionHandler

    @Before
    fun `Set up`() {
        geocodeGatewayExceptionHandler = GeocodeGatewayExceptionHandler()
    }

    @Test
    fun `Sets http response status for GeocodeGatewayException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        geocodeGatewayExceptionHandler.handleException(serverWebExchangeMock, GeocodeGatewayException("message"))

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Checks if exception type is GeocodeGatewayException`() {
        assertTrue(geocodeGatewayExceptionHandler.canHandle(GeocodeGatewayException("message")))
    }

}