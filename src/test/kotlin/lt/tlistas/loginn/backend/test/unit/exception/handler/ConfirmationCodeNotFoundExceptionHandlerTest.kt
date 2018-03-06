package lt.tlistas.loginn.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.crowbar.exception.ConfirmationCodeNotFoundException
import lt.tlistas.loginn.backend.exception.handler.ConfirmationCodeNotFoundExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class ConfirmationCodeNotFoundExceptionHandlerTest {

    private lateinit var confirmationCodeNotFoundExceptionHandler: ConfirmationCodeNotFoundExceptionHandler

    @Before
    fun `Set up`() {
        confirmationCodeNotFoundExceptionHandler = ConfirmationCodeNotFoundExceptionHandler()
    }

    @Test
    fun `Sets http response status for ConfirmationCodeException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        confirmationCodeNotFoundExceptionHandler.handleException(serverWebExchangeMock, ConfirmationCodeNotFoundException())

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Checks if exception type is ConfirmationCodeException`() {
        assertTrue(confirmationCodeNotFoundExceptionHandler.canHandle(ConfirmationCodeNotFoundException()))
    }

}