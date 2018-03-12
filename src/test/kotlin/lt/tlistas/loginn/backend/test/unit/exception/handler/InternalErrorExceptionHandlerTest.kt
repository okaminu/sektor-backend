package lt.tlistas.loginn.backend.test.unit.exception.handler

import com.amazonaws.services.sns.model.InternalErrorException
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.loginn.backend.exception.handler.InternalErrorExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class InternalErrorExceptionHandlerTest {

    private lateinit var handler: InternalErrorExceptionHandler

    @Before
    fun `Set up`() {
        handler = InternalErrorExceptionHandler()
    }

    @Test
    fun `Sets http response status for InternalErrorException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        handler.handleException(serverWebExchangeMock, InternalErrorException("message"))

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `Checks if exception type is InternalErrorException`() {
        assertTrue(handler.canHandle(InternalErrorException("message")))
    }

}