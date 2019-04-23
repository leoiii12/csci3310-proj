/**
* event331-api
* No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
*
* OpenAPI spec version: 0.0.5
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package io.swagger.client.apis

import io.swagger.client.models.CreateSightInput
import io.swagger.client.models.CreateSightOutput
import io.swagger.client.models.GetSightInput
import io.swagger.client.models.GetSightOutput
import io.swagger.client.models.ListSightsOutput

import io.swagger.client.infrastructure.*

class SightApi(basePath: kotlin.String = "https://event331-api.azurewebsites.net") : ApiClient(basePath) {

    /**
    * 
    * 
    * @param input  
    * @param xAuthorization  (optional, default to )
    * @return CreateSightOutput
    */
    @Suppress("UNCHECKED_CAST")
    fun apiSightCreate(input: CreateSightInput, xAuthorization: kotlin.String) : CreateSightOutput {
        val localVariableBody: kotlin.Any? = input
        val localVariableQuery: MultiValueMap = mapOf()
        
        val contentHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf()
        val acceptsHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf("Accept" to "application/json")
        val localVariableHeaders: kotlin.collections.MutableMap<kotlin.String,kotlin.String> = mutableMapOf("X-Authorization" to xAuthorization)
        localVariableHeaders.putAll(contentHeaders)
        localVariableHeaders.putAll(acceptsHeaders)
        
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/Sight/Create",
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val response = request<CreateSightOutput>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as CreateSightOutput
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
            else -> throw kotlin.IllegalStateException("Undefined ResponseType.")
        }
    }

    /**
    * 
    * 
    * @param input  
    * @param xAuthorization  (optional, default to )
    * @return GetSightOutput
    */
    @Suppress("UNCHECKED_CAST")
    fun apiSightGet(input: GetSightInput, xAuthorization: kotlin.String) : GetSightOutput {
        val localVariableBody: kotlin.Any? = input
        val localVariableQuery: MultiValueMap = mapOf()
        
        val contentHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf()
        val acceptsHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf("Accept" to "application/json")
        val localVariableHeaders: kotlin.collections.MutableMap<kotlin.String,kotlin.String> = mutableMapOf("X-Authorization" to xAuthorization)
        localVariableHeaders.putAll(contentHeaders)
        localVariableHeaders.putAll(acceptsHeaders)
        
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/Sight/Get",
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val response = request<GetSightOutput>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as GetSightOutput
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
            else -> throw kotlin.IllegalStateException("Undefined ResponseType.")
        }
    }

    /**
    * 
    * 
    * @param xAuthorization  (optional, default to )
    * @return ListSightsOutput
    */
    @Suppress("UNCHECKED_CAST")
    fun apiSightList(xAuthorization: kotlin.String) : ListSightsOutput {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mapOf()
        
        val contentHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf()
        val acceptsHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf("Accept" to "application/json")
        val localVariableHeaders: kotlin.collections.MutableMap<kotlin.String,kotlin.String> = mutableMapOf("X-Authorization" to xAuthorization)
        localVariableHeaders.putAll(contentHeaders)
        localVariableHeaders.putAll(acceptsHeaders)
        
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/Sight/List",
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val response = request<ListSightsOutput>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as ListSightsOutput
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
            else -> throw kotlin.IllegalStateException("Undefined ResponseType.")
        }
    }

}
