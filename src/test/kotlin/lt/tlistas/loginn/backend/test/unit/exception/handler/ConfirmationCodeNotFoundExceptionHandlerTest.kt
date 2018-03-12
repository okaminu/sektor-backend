package lt.tlistas.loginn.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.exception.IncorrectConfirmationCodeException
import lt.tlistas.loginn.backend.exception.handler.IncorrectConfirmationCodeExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class ConfirmationCodeNotFoundExceptionHandlerTest {

    private lateinit var handler: IncorrectConfirmationCodeExceptionHandler

    @Before
    fun `Set up`() {
        handler = IncorrectConfirmationCodeExceptionHandler()
    }

    @Test
    fun `Sets http response status for IncorrectConfirmationCodeException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        handler
                .handleException(serverWebExchangeMock, IncorrectConfirmationCodeException())

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Checks if exception type is IncorrectConfirmationCodeException`() {
        assertTrue(handler.canHandle(IncorrectConfirmationCodeException()))
    }

}