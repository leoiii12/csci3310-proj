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

import io.swagger.client.models.UserDto

/**
 * 
 * @param id 
 * @param content 
 * @param createUser 
 * @param createDate 
 */
data class CommentDto (
    val id: kotlin.Int? = null,
    val content: kotlin.String? = null,
    val createUser: UserDto? = null,
    val createDate: kotlin.String? = null
) {

}

