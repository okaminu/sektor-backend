package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.exception.LocationByAddressNotFoundException
import lt.tlistas.core.exception.LocationGatewayException
import lt.tlistas.loginn.backend.ExceptionHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class ExceptionHandlerTest {

    private lateinit var exceptionHandler: ExceptionHandler

    @Before
    fun setUp() {
        exceptionHandler = ExceptionHandler()
    }

    @Test
    fun `Converts GeocodeGatewayException to response status`() {
        val locationGatewayException = LocationGatewayException()
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handlingIsSuccessful = exceptionHandler.handle(serverWebExchangeMock, locationGatewayException)

        assertEquals(Mono.empty(), handlingIsSuccessful)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Converts LocationNotFoundException to response status`() {
        val locationGatewayException = LocationByAddressNotFoundException()
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handlingIsSuccessful = exceptionHandler.handle(serverWebExchangeMock, locationGatewayException)

        assertEquals(Mono.empty(), handlingIsSuccessful)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNPROCESSABLE_ENTITY)
    }

}