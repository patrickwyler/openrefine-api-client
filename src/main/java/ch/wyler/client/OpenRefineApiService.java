package ch.wyler.client;

import ch.wyler.client.dtos.GetCsrfTokenResponse;
import ch.wyler.client.dtos.GetProjectMetadataResponse;
import ch.wyler.client.dtos.GetVersionResponse;
import ch.wyler.client.dtos.ResultResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * https://docs.openrefine.org/technical-reference/openrefine-api
 */
public interface OpenRefineApiService {

    @GET("/command/core/get-version")
    Call<GetVersionResponse> getVersion();

    @GET("/command/core/get-csrf-token")
    Call<GetCsrfTokenResponse> getCsrfToken();

    @GET("/command/core/get-project-metadata")
    Call<GetProjectMetadataResponse> getProjectMetadata(@Query("project") String projectId);

    @Multipart
    @POST("/command/core/create-project-from-upload")
    Call<ResponseBody> createProjectFromUpload(
            @Part("project-name") RequestBody projectName,
            @Part MultipartBody.Part file);

    @POST("/command/core/delete-project")
    Call<ResultResponse> deleteProject(@Query("project") String projectId);

    @FormUrlEncoded
    @POST("/command/core/apply-operations")
    Call<ResultResponse> applyOperations(@Query("project") String projectId, @Field("operations") String operations);

    @FormUrlEncoded
    @POST("/command/core/export-rows")
    Call<ResponseBody> exportRows(@Query("project") String projectId, @Query("format") String format, @Field("operations") String engine);
}
