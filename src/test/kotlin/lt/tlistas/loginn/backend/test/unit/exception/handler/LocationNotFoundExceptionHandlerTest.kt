package lt.tlistas.loginn.backend.test.unit.exception.handler

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import lt.tlistas.core.api.exception.LocationNotFoundException
import lt.tlistas.loginn.backend.exception.handler.LocationNotFoundExceptionHandler
import org.junit.Before
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import kotlin.test.assertTrue

class LocationNotFoundExceptionHandlerTest {

    private lateinit var locationNotFoundExceptionHandler: LocationNotFoundExceptionHandler

    @Before
    fun `Set up`() {
        locationNotFoundExceptionHandler = LocationNotFoundExceptionHandler()
    }

    @Test
    fun `Sets http response status for LocationNotFoundException`() {
        val serverWebExchangeMock = mock<ServerWebExchange>()
        val serverHttpResponseMock = mock<ServerHttpResponse>()
        doReturn(serverHttpResponseMock).`when`(serverWebExchangeMock).response

        locationNotFoundExceptionHandler.handleException(serverWebExchangeMock,
                LocationNotFoundException("message"))

        verify(serverHttpResponseMock).statusCode = eq(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun `Checks if exception type is LocationNotFoundException`() {
        assertTrue(locationNotFoundExceptionHandler.canHandle(LocationNotFoundException("message")))
    }

}