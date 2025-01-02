package demo.example

import burp.api.montoya.BurpExtension
import burp.api.montoya.MontoyaApi
import burp.api.montoya.http.message.HttpRequestResponse
import burp.api.montoya.persistence.PersistedList
import burp.api.montoya.proxy.http.*

class Extension : BurpExtension, ProxyResponseHandler {
    private lateinit var api: MontoyaApi
    private val requestResponses = mutableListOf<HttpRequestResponse>()
    private val requestResponsesKey = "REQUEST_RESPONSES"

    override fun initialize(api: MontoyaApi) {
        this.api = api
        this.api.extension().setName("class cast exception demo")

        this.api.proxy().registerResponseHandler(this)
        this.api.logging().logToOutput("Test logging")

        val retrievedRequestResponses = mutableListOf<HttpRequestResponse>()
        this.api.persistence().extensionData().getHttpRequestResponseList(requestResponsesKey)?.let {
            this.api.logging().logToOutput("Retrieved ${it.count()} requestResponses")
            retrievedRequestResponses.addAll(it)
        }

        try {
            retrievedRequestResponses.forEach {
                this.api.logging().logToOutput("request url ${it.request().url()}")
            }
        } catch (e: Exception) {
            this.api.logging().logToError("Error while retrieving: %s - %s".format(e.javaClass.name, e.message))
        }

        this.api.extension().registerUnloadingHandler {
            try {
                val persistedRequestResponses = PersistedList.persistedHttpRequestResponseList()
                persistedRequestResponses.addAll(requestResponses)
                this.api.persistence().extensionData()
                    .setHttpRequestResponseList(requestResponsesKey, persistedRequestResponses)

                this.api.logging().logToOutput("Persisted ${persistedRequestResponses.count()} requestResponses")
            } catch (e: Exception) {
                this.api.logging().logToError("Error while persisting: %s - %s".format(e.javaClass.name, e.message))
            }
        }
    }

    override fun handleResponseReceived(interceptedResponse: InterceptedResponse): ProxyResponseReceivedAction {
        if (interceptedResponse.request().isInScope) {
            requestResponses.add(
                HttpRequestResponse.httpRequestResponse(
                    interceptedResponse.request(),
                    interceptedResponse
                )
            )
        }

        return ProxyResponseReceivedAction.continueWith(interceptedResponse)
    }

    override fun handleResponseToBeSent(interceptedResponse: InterceptedResponse): ProxyResponseToBeSentAction {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse)
    }
}