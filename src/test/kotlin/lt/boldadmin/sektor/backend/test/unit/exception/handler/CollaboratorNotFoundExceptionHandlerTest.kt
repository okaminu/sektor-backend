package lt.boldadmin.sektor.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.*
import lt.boldadmin.nexus.api.exception.CollaboratorNotFoundException
import lt.boldadmin.sektor.backend.exception.handler.CollaboratorNotFoundExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class CollaboratorNotFoundExceptionHandlerTest {

    private lateinit var handler: CollaboratorNotFoundExceptionHandler

    @Before
    fun `Set up`() {
        handler = CollaboratorNotFoundExceptionHandler()
    }

    @Test
    fun `Sets http response status for CollaboratorNotFoundException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        handler.handleException(serverWebExchangeMock,
                CollaboratorNotFoundException("")
        )

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Checks if exception type is CollaboratorNotFoundException`() {
        assertTrue(handler.canHandle(CollaboratorNotFoundException("")))
    }

}