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
 * @param skip @IsDefined() @IsInt() @IsDivisibleBy(20)
 * @param name @IsOptional() @IsString() @Length(2)
 * @param sex @IsOptional() @IsInt()
 */
data class ListUsersInput (
    /* @IsDefined() @IsInt() @IsDivisibleBy(20) */
    val skip: java.math.BigDecimal? = null,
    /* @IsOptional() @IsString() @Length(2) */
    val name: kotlin.String? = null,
    /* @IsOptional() @IsInt() */
    val sex: java.math.BigDecimal? = null
) {

}

