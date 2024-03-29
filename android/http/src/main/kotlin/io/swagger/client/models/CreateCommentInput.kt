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
 * @param content @IsDefined() @IsString() @Length(2)
 * @param flightId @IsOptional() @IsInt()
 * @param sightId @IsOptional() @IsInt()
 * @param hotelId @IsOptional() @IsInt()
 */
data class CreateCommentInput (
    /* @IsDefined() @IsString() @Length(2) */
    val content: kotlin.String? = null,
    /* @IsOptional() @IsInt() */
    val flightId: kotlin.Int? = null,
    /* @IsOptional() @IsInt() */
    val sightId: kotlin.Int? = null,
    /* @IsOptional() @IsInt() */
    val hotelId: kotlin.Int? = null
) {

}

