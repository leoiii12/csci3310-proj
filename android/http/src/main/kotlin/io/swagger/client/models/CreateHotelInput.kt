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
package io.swagger.client.models


/**
 * 
 * @param title @IsDefined() @IsString() @Length(2)
 * @param lat @IsOptional() @IsNumber()
 * @param lng @IsOptional() @IsNumber()
 * @param imageIds @IsOptional() @IsInt({ each: true })
 */
data class CreateHotelInput (
    /* @IsDefined() @IsString() @Length(2) */
    val title: kotlin.String? = null,
    /* @IsOptional() @IsNumber() */
    val lat: kotlin.Double? = null,
    /* @IsOptional() @IsNumber() */
    val lng: kotlin.Double? = null,
    /* @IsOptional() @IsInt({ each: true }) */
    val imageIds: kotlin.Array<kotlin.Int>? = null
) {

}

