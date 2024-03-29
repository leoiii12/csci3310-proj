/**
* event331-api
* No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
*
* OpenAPI spec version: 0.0.6
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package io.swagger.client.apis

import io.swagger.client.models.CreateHotelInput
import io.swagger.client.models.CreateHotelOutput
import io.swagger.client.models.GetHotelInput
import io.swagger.client.models.GetHotelOutput
import io.swagger.client.models.ListHotelsOutput

import io.swagger.client.infrastructure.*

class HotelApi(basePath: kotlin.String = "https://event331-api.azurewebsites.net") : ApiClient(basePath) {

    /**
    * 
    * 
    * @param input  
    * @param xAuthorization  (optional, default to )
    * @return CreateHotelOutput
    */
    @Suppress("UNCHECKED_CAST")
    fun apiHotelCreate(input: CreateHotelInput, xAuthorization: kotlin.String) : CreateHotelOutput {
        val localVariableBody: kotlin.Any? = input
        val localVariableQuery: MultiValueMap = mapOf()
        
        val contentHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf()
        val acceptsHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf("Accept" to "application/json")
        val localVariableHeaders: kotlin.collections.MutableMap<kotlin.String,kotlin.String> = mutableMapOf("X-Authorization" to xAuthorization)
        localVariableHeaders.putAll(contentHeaders)
        localVariableHeaders.putAll(acceptsHeaders)
        
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/Hotel/Create",
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val response = request<CreateHotelOutput>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as CreateHotelOutput
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
    * @return GetHotelOutput
    */
    @Suppress("UNCHECKED_CAST")
    fun apiHotelGet(input: GetHotelInput, xAuthorization: kotlin.String) : GetHotelOutput {
        val localVariableBody: kotlin.Any? = input
        val localVariableQuery: MultiValueMap = mapOf()
        
        val contentHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf()
        val acceptsHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf("Accept" to "application/json")
        val localVariableHeaders: kotlin.collections.MutableMap<kotlin.String,kotlin.String> = mutableMapOf("X-Authorization" to xAuthorization)
        localVariableHeaders.putAll(contentHeaders)
        localVariableHeaders.putAll(acceptsHeaders)
        
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/Hotel/Get",
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val response = request<GetHotelOutput>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as GetHotelOutput
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
    * @return ListHotelsOutput
    */
    @Suppress("UNCHECKED_CAST")
    fun apiHotelList(xAuthorization: kotlin.String) : ListHotelsOutput {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mapOf()
        
        val contentHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf()
        val acceptsHeaders: kotlin.collections.Map<kotlin.String,kotlin.String> = mapOf("Accept" to "application/json")
        val localVariableHeaders: kotlin.collections.MutableMap<kotlin.String,kotlin.String> = mutableMapOf("X-Authorization" to xAuthorization)
        localVariableHeaders.putAll(contentHeaders)
        localVariableHeaders.putAll(acceptsHeaders)
        
        val localVariableConfig = RequestConfig(
            RequestMethod.POST,
            "/api/Hotel/List",
            query = localVariableQuery,
            headers = localVariableHeaders
        )
        val response = request<ListHotelsOutput>(
            localVariableConfig,
            localVariableBody
        )

        return when (response.responseType) {
            ResponseType.Success -> (response as Success<*>).data as ListHotelsOutput
            ResponseType.Informational -> TODO()
            ResponseType.Redirection -> TODO()
            ResponseType.ClientError -> throw ClientException((response as ClientError<*>).body as? String ?: "Client error")
            ResponseType.ServerError -> throw ServerException((response as ServerError<*>).message ?: "Server error")
            else -> throw kotlin.IllegalStateException("Undefined ResponseType.")
        }
    }

}
