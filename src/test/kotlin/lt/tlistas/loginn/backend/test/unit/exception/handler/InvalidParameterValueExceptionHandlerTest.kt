package lt.tlistas.loginn.backend.test.unit.exception.handler

import com.amazonaws.services.sns.model.InvalidParameterException
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.loginn.backend.exception.handler.InvalidParameterValueExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class InvalidParameterValueExceptionHandlerTest {

    private lateinit var invalidParameterValueExceptionHandler: InvalidParameterValueExceptionHandler

    @Before
    fun `Set up`() {
        invalidParameterValueExceptionHandler = InvalidParameterValueExceptionHandler()
    }

    @Test
    fun `Sets http response status for InvalidParameterException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        invalidParameterValueExceptionHandler.handleException(serverWebExchangeMock,
                InvalidParameterException("message"))

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `Checks if exception type is InvalidParameterException`() {
        assertTrue(invalidParameterValueExceptionHandler.canHandle(InvalidParameterException("message")))
    }

}