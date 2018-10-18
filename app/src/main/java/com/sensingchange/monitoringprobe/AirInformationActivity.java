package com.sensingchange.monitoringprobe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sensingchange.monitoringprobe.model.AirInformation;
import com.sensingchange.monitoringprobe.model.User;
import com.sensingchange.monitoringprobe.model.UserModel;
import com.sensingchange.monitoringprobe.remote.InformationService;
import com.sensingchange.monitoringprobe.remote.RetrofitClient;
import com.sensingchange.monitoringprobe.remote.UserService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class AirInformationActivity extends AppCompatActivity {
    private TextView mTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_information);
        mTimestamp = findViewById(R.id.txt_timestamp);

        InformationService service = RetrofitClient.getClient().create(InformationService.class);

        /*Call the method with parameter in the interface to get the user login*/
        Call<AirInformation> call = service.auth("application/json");

        call.enqueue(new Callback<AirInformation>() {

            public void onResponse(Call<AirInformation> call, Response<AirInformation> response) {
                Integer status = response.code();
                if (status.equals(200)) {
                    mTimestamp.setText(response.body().getTimestamp());
                } else {
                    Toast.makeText(AirInformationActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<AirInformation> call, Throwable t) {
                Context context = AirInformationActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(t.toString());
                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
