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
package io.swagger.client.models


/**
 * 
 * @param id 
 * @param emailAddress 
 * @param firstName 
 * @param lastName 
 * @param sex 
 * @param roles 
 */
data class MyUserDto (
    val id: kotlin.Int? = null,
    val emailAddress: kotlin.String? = null,
    val firstName: kotlin.String? = null,
    val lastName: kotlin.String? = null,
    val sex: kotlin.Int? = null,
    val roles: kotlin.Array<kotlin.Int>? = null
) {

}

