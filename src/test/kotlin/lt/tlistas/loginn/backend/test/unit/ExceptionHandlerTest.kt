package lt.tlistas.loginn.backend.test.unit

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.api.exception.*
import lt.tlistas.loginn.backend.ExceptionHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.dao.EmptyResultDataAccessException
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
    fun `Handles GeocodeGatewayException by setting http response status and returning response body`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handleResult = exceptionHandler.handle(serverWebExchangeMock, GeocodeGatewayException("message"))

        assertEquals(Mono.empty(), handleResult)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Handles LocationNotFoundException by setting http response status and returning response body`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handleResult = exceptionHandler.handle(serverWebExchangeMock, LocationNotFoundException("message"))

        assertEquals(Mono.empty(), handleResult)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `Handles EmptyResultDataAccessException by setting http response status and returning response body`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handleResult = exceptionHandler.handle(serverWebExchangeMock, mock<EmptyResultDataAccessException>())

        assertEquals(Mono.empty(), handleResult)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Handles SmsGatewayException by setting http response status and returning response body`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handleResult = exceptionHandler.handle(serverWebExchangeMock, mock<SmsGatewayException>())

        assertEquals(Mono.empty(), handleResult)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Handles InvalidMobileNumberException by setting http response status and returning response body`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handleResult = exceptionHandler.handle(serverWebExchangeMock, mock<InvalidMobileNumberException>())

        assertEquals(Mono.empty(), handleResult)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun `Handles InvalidCodeNumberException by setting http response status and returning response body`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        val handleResult = exceptionHandler.handle(serverWebExchangeMock, mock<InvalidCodeException>())

        assertEquals(Mono.empty(), handleResult)
        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.BAD_REQUEST)
    }

}