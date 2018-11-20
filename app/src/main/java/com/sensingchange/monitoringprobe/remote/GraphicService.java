package com.sensingchange.monitoringprobe.remote;

import com.sensingchange.monitoringprobe.model.graph.AirData;
import com.sensingchange.monitoringprobe.model.graph.SoilData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GraphicService {
    @GET("data/soil_informations/{period}")
    Call<SoilData> getSoilData (@Header("Content-Type") String contentType, @Path("period") String period);

    @GET("data/air_informations/{period}")
    Call<AirData> getAirData (@Header("Content-Type") String contentType, @Path("period") String period);
}
