package lt.boldadmin.sektor.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.boldadmin.sektor.backend.exception.WorkLogIntervalDoesNotBelongToCollaboratorException
import lt.boldadmin.sektor.backend.exception.handler.WorkLogDoesNotBelongExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class WorkLogDoesNotBelongExceptionHandlerTest {

    private lateinit var handler: WorkLogDoesNotBelongExceptionHandler

    @Before
    fun `Set up`() {
        handler = WorkLogDoesNotBelongExceptionHandler()
    }

    @Test
    fun `Sets http response status for WorkLogDoesNotBelongExceptionHandler`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        handler.handleException(serverWebExchangeMock,
                WorkLogIntervalDoesNotBelongToCollaboratorException())

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Checks if exception type is WorkLogDoesNotBelongExceptionHandler`() {
        assertTrue(handler.canHandle(WorkLogIntervalDoesNotBelongToCollaboratorException()))
    }

}