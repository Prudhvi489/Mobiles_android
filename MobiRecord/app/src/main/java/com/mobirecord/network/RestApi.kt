package com.tuneconnect.network

import com.mobirecord.network.ApiResult
import com.mobirecord.ui.addmobiles.model.AwsUploadUrlResponse
import com.mobirecord.ui.login.model.LoginResponse
import com.mobirecord.ui.mobiledetails.model.Asset
import com.mobirecord.ui.mobiledetails.model.GetAssetsResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface
RestApi {

    @GET(RESTURLS.getCountries)
    suspend fun getCountries(
        @HeaderMap headerMap: Map<String, String>,
    ): Response<ApiResult<Any>>

    @POST(RESTURLS.login)
    suspend fun login(
        @Body jsonObject: RequestBody,
    ): Response<ApiResult<LoginResponse?>?>

    @POST(RESTURLS.create_asset)
    suspend fun createAsset(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body jsonObject: RequestBody,
    ): Response<ApiResult<Any?>?>

    @DELETE(RESTURLS.deleteAsset)
    suspend fun deleteAsset(
        @Path("assetId") assetId: String,
         @HeaderMap headerMap: HashMap<String, String>
    ): Response<ApiResult<Any?>?>

    @POST(RESTURLS.get_assets)
    suspend fun getAssets(
        @Body jsonObject: RequestBody,
        @HeaderMap headerMap: HashMap<String, String>
    ): Response<ApiResult<GetAssetsResponse?>?>


    @GET(RESTURLS.get_asset)
    suspend fun getAssetById(
        @Path("assetId") assetId: String,
        @HeaderMap headerMap: HashMap<String, String>
    ): Response<ApiResult<Asset?>?>

    @PATCH(RESTURLS.update_asset)
    suspend fun updateAsset(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body jsonObject: RequestBody,
    ): Response<ApiResult<Any?>?>


    @Multipart
    @POST(RESTURLS.uploadSelfiePic)
    suspend fun uploadSelfiePic(
        @HeaderMap headerMap: HashMap<String, String>,
        @Part image: MultipartBody.Part,
    ): Response<ApiResult<Any>?>

    @FormUrlEncoded
    @POST("token")
    fun accessTokenApiIntheAbsenceofSpotifyApp(
        @HeaderMap headerMap: HashMap<String, String>,
        @Field("grant_type") grant_type: String?,
        @Field("redirect_uri") redirect_uri: String?,
    ): Call<Any?>

    @GET(RESTURLS.search)
    fun spotifySearch(
        @HeaderMap headerMap: HashMap<String, String>,
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): Call<Any>


    @Multipart
    @POST(RESTURLS.updateCelebrityDetails)
    suspend fun updateCelebrityDetails(
        @HeaderMap headerMap: HashMap<String, String>,
        @Part("documentType") documentType: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part("countryId") countryId: RequestBody,
        @Part("description") description: RequestBody,
        @Part("categoryOther") categoryOther: RequestBody,
        @Part("socialMediaLinks") jsonPayload: RequestBody, // JSON payload as a part
        @Part image: MultipartBody.Part,

        ): Response<ApiResult<Any>?>

    @GET(RESTURLS.refreshToken)
    fun refreshToken(
        @HeaderMap headerMap: Map<String, String>,
    ): Call<Any?>

    @POST(RESTURLS.generateUploadrls)
    suspend fun generateUploadrls(
        @HeaderMap headers: HashMap<String, String>, @Body jsonObject: RequestBody
    ): Response<ApiResult<AwsUploadUrlResponse?>?>
    @PUT
    fun uploadFile(@Url url: String, @Body file: RequestBody): Call<ResponseBody>
}
