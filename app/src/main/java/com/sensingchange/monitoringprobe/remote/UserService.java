package com.sensingchange.monitoringprobe.remote;

import com.sensingchange.monitoringprobe.model.User;
import com.sensingchange.monitoringprobe.model.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {
    @POST("sessions/")
    Call<User> auth (@Header("Content-Type") String contentType, @Body UserModel user);

}