package com.sensingchange.monitoringprobe.remote;

public class ApiUtils {

    public static final String BASE_URL = "http://192.168.0.116:3000/api/v1/";

    public static UserService getUserService(){
        return RetrofitClient.getClient().create(UserService.class);
    }
}
