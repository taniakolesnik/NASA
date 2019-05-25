package uk.co.taniakolesnik.nasa.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.co.taniakolesnik.nasa.module.ApodResult;

public interface Client {
    @GET("planetary/apod")
    Call<ApodResult> getApod(
            @Query("api_key") String api_key,
            @Query("date") String date,
            @Query("hd") boolean isHD
    );
}
