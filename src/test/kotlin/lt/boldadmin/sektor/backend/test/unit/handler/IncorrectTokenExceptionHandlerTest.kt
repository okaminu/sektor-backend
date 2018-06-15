package lt.boldadmin.sektor.backend.test.unit.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.sektor.backend.exception.IncorrectTokenException
import lt.boldadmin.sektor.backend.exception.handler.IncorrectTokenExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class IncorrectTokenExceptionHandlerTest {

    private lateinit var handler: IncorrectTokenExceptionHandler

    @Before
    fun `Set up`() {
        handler = IncorrectTokenExceptionHandler()
    }

    @Test
    fun `Sets http response status for IncorrectTokenException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        handler.handleException(serverWebExchangeMock, IncorrectTokenException())

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Checks if exception type is IncorrectTokenException`() {
        assertTrue(handler.canHandle(IncorrectTokenException()))
    }

}