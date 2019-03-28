/**
* event331-api
* No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
*
* OpenAPI spec version: 0.0.2
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package io.swagger.client.models


/**
 * 
 * @param emailAddress @IsDefined() @IsEmail()
 * @param password @IsDefined() @Length(6)
 * @param firstName @IsOptional() @IsString()
 * @param lastName @IsOptional() @IsString()
 * @param sex @IsOptional() @IsInt()
 */
data class SignUpInput (
    /* @IsDefined() @IsEmail() */
    val emailAddress: kotlin.String? = null,
    /* @IsDefined() @Length(6) */
    val password: kotlin.String? = null,
    /* @IsOptional() @IsString() */
    val firstName: kotlin.String? = null,
    /* @IsOptional() @IsString() */
    val lastName: kotlin.String? = null,
    /* @IsOptional() @IsInt() */
    val sex: kotlin.Int? = null
) {

}

