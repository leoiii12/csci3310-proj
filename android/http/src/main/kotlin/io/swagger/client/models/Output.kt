/**
* event331-api
* No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
*
* OpenAPI spec version: 0.0.1
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package io.swagger.client.models


/**
 * 
 * @param success 
 * @param &#x60;data&#x60; 
 * @param message 
 * @param dateTime 
 */
data class Output<T> (
    val success: kotlin.Boolean? = null,
    val `data`: T? = null,
    val message: kotlin.String? = null,
    val dateTime: kotlin.String? = null
) {

}

