package lt.boldadmin.sektor.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.sektor.backend.exception.IncorrectConfirmationCodeException
import lt.boldadmin.sektor.backend.exception.handler.IncorrectConfirmationCodeExceptionHandler
import lt.boldadmin.sektor.backend.exception.handler.TemplateExceptionHandler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IncorrectConfirmationCodeExceptionHandlerTest {

    @Rule
    @JvmField
    val expectedException = ExpectedException.none()!!

    private lateinit var handler: IncorrectConfirmationCodeExceptionHandler

    @Before
    fun `Set up`() {
        handler = IncorrectConfirmationCodeExceptionHandler()
    }

    @Test
    fun `Handles exception`() {
        val serverWebExchangeStub = mock<ServerWebExchange>()
        val serverHttpResponseDummy = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseDummy).`when`(serverWebExchangeStub).response

        val response = handler.handle(serverWebExchangeStub, IncorrectConfirmationCodeException())

        assertEquals(Mono.empty(), response)
    }

    @Test
    fun `Returns error when exception could not be handled`() {
        expectedException.expect(Exception::class.java)
        val templateExceptionHandlerStub: TemplateExceptionHandler = spy()
        doReturn(false).`when`(templateExceptionHandlerStub).canHandle(any())

        val response = handler.handle(mock(), mock<Exception>())
        response.block()
    }
    @Test
    fun `Logs an exception`() {
        val errorSpy = mock<Exception>()

        handler.handle(mock(), errorSpy)

        verify(errorSpy).printStackTrace()
    }

    @Test
    fun `Sets http response status for IncorrectConfirmationCodeException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        handler
                .handleException(serverWebExchangeMock,
                        IncorrectConfirmationCodeException()
                )

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Checks if exception type is IncorrectConfirmationCodeException`() {
        assertTrue(handler.canHandle(IncorrectConfirmationCodeException()))
    }

}