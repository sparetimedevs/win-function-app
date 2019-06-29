package com.sparetimedevs.win

import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpStatus
import com.sparetimedevs.HttpResponseMessageMock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import java.util.Optional
import java.util.logging.Logger

/**
 * Unit test for Function class.
 */
class FunctionTest {

    inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)

    private fun testHttpTrigger(httpMethod: HttpMethod) {
        // Setup
        val req = mock<HttpRequestMessage<Optional<String>>>()

        val queryParams = HashMap<String, String>()
        queryParams["name"] = "Azure"
        doReturn(queryParams).`when`<HttpRequestMessage<Optional<String>>>(req).queryParameters

        val queryBody = Optional.empty<String>()
        doReturn(queryBody).`when`<HttpRequestMessage<*>>(req).body
        doReturn(httpMethod).`when`<HttpRequestMessage<*>>(req).httpMethod

        doAnswer { invocation ->
            val status = invocation.arguments[0] as HttpStatus
            HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status)
        }.`when`<HttpRequestMessage<*>>(req).createResponseBuilder(any(HttpStatus::class.java))

        val context = mock(ExecutionContext::class.java)
        doReturn(Logger.getGlobal()).`when`(context).logger

        // Invoke
        val ret = Function().run(req, context)

        // Verify
        assertEquals(ret.status, HttpStatus.OK)
    }

    /**
     * Unit test for HttpTrigger GET method.
     */
    @Test
    @Throws(Exception::class)
    fun testHttpTriggerGET() {
        testHttpTrigger(HttpMethod.GET)
    }

    /**
     * Unit test for HttpTrigger POST method.
     */
    @Test
    @Throws(Exception::class)
    fun testHttpTriggerPOST() {
        testHttpTrigger(HttpMethod.POST)
    }

}
