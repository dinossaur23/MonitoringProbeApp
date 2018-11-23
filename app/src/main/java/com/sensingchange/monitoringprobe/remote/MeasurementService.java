package com.sensingchange.monitoringprobe.remote;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sensingchange.monitoringprobe.model.measurement.Post;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Body;

public interface MeasurementService {
    @POST("measurements")
    Call<Post> postMeasurement (@Header("Content-Type") String contentType, @Body JsonElement body);

}
