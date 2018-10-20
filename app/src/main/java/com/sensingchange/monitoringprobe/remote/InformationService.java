package com.sensingchange.monitoringprobe.remote;

import com.sensingchange.monitoringprobe.model.AirInformation;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.GET;

public interface InformationService {
    // Air information
    @GET("data/probe_information")
    Call<AirInformation> auth (@Header("Content-Type") String contentType);

}