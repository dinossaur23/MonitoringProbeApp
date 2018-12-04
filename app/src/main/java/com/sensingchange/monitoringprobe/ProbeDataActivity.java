package com.sensingchange.monitoringprobe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sensingchange.monitoringprobe.model.AirInformation;
import com.sensingchange.monitoringprobe.remote.InformationService;
import com.sensingchange.monitoringprobe.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProbeDataActivity extends AppCompatActivity {
    private TextView mTimestamp;
    private TextView mAirHumidityValue;
    private TextView mAirHumidityUnity;
    private TextView mAirTemperatureValue;
    private TextView mAirTemperatureUnity;
    private TextView mAirLuminosityValue;
    private TextView mAirLuminosityUnity;
    private TextView mSoilHumidityValue;
    private TextView mSoilHumidityUnity;
    private TextView mSoilTemperatureValue;
    private TextView mSoilTemperatureUnity;
    private TextView mSoilCapacityValue;
    private TextView mSoilCapacityUnity;
    private TextView mWeatherRainfallHourValue;
    private TextView mWeatherRainfallHourUnity;
    private TextView mWeatherRainfallDayValue;
    private TextView mWeatherRainfallDayUnity;
    private TextView mWeatherWindDirectionValue;
    private TextView mWeatherWindDirectionDirection;
    private TextView mWeatherWindSpeedAverageValue;
    private TextView mWeatherWindSpeedAverageUnity;
    private TextView mWeatherWindSpeedMaxValue;
    private TextView mWeatherWindSpeedMaxUnity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probe_data);
        mTimestamp = findViewById(R.id.txt_timestamp);

        mAirHumidityValue = findViewById(R.id.txt_air_humidity_value);
        mAirHumidityUnity = findViewById(R.id.txt_air_humidity_unity);

        mAirTemperatureValue = findViewById(R.id.txt_air_temperature_value);
        mAirTemperatureUnity = findViewById(R.id.txt_air_temperature_unity);

        mAirLuminosityValue = findViewById(R.id.txt_air_luminosity_value);
        mAirLuminosityUnity = findViewById(R.id.txt_air_luminosity_unity);

        mSoilHumidityValue = findViewById(R.id.txt_soil_humidity_value);
        mSoilHumidityUnity = findViewById(R.id.txt_soil_humidity_unity);

        mSoilTemperatureValue = findViewById(R.id.txt_soil_temperature_value);
        mSoilTemperatureUnity = findViewById(R.id.txt_soil_temperature_unity);

        mSoilCapacityValue = findViewById(R.id.txt_soil_capacity_value);
        mSoilCapacityUnity = findViewById(R.id.txt_soil_capacity_unity);

        mWeatherRainfallHourValue = findViewById(R.id.txt_weather_raindrop_hour_value);
        mWeatherRainfallHourUnity = findViewById(R.id.txt_weather_raindrop_hour_unity);

        mWeatherRainfallDayValue = findViewById(R.id.txt_weather_raindrop_day_value);
        mWeatherRainfallDayUnity = findViewById(R.id.txt_weather_raindrop_day_unity);

        mWeatherWindDirectionValue = findViewById(R.id.txt_weather_wind_direction_value);
        mWeatherWindDirectionDirection = findViewById(R.id.txt_weather_wind_direction_direction);

        mWeatherWindSpeedAverageValue = findViewById(R.id.txt_weather_wind_speed_average_value);
        mWeatherWindSpeedAverageUnity = findViewById(R.id.txt_weather_wind_speed_average_unity);

        mWeatherWindSpeedMaxValue = findViewById(R.id.txt_weather_wind_speed_max_value);
        mWeatherWindSpeedMaxUnity = findViewById(R.id.txt_weather_wind_speed_max_unity);

        InformationService service = RetrofitClient.getClient().create(InformationService.class);

        /*Call the method with parameter in the interface to get the user login*/
        Call<AirInformation> call = service.auth("application/json");

        call.enqueue(new Callback<AirInformation>() {

            public void onResponse(Call<AirInformation> call, Response<AirInformation> response) {
                Integer status = response.code();
                if (status.equals(200)) {
                    // set all text views
                    setValuesOnView(response.body());
                } else {
                    Toast.makeText(ProbeDataActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<AirInformation> call, Throwable t) {
                Context context = ProbeDataActivity.this;
//                Developer version
//
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append("Error: ");
//                stringBuilder.append(t.toString());
//                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Server error. Please go back end try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View v){
        Intent intent = new Intent(ProbeDataActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void setValuesOnView(AirInformation response) {
        mTimestamp.setText(response.getTimestamp());

        mAirHumidityValue.setText(response.getAirHumidity().getValue());
        mAirHumidityUnity.setText(response.getAirHumidity().getUnity());

        mAirTemperatureValue.setText(response.getAirTemperature().getValue());
        mAirTemperatureUnity.setText(response.getAirTemperature().getUnity());

        mAirLuminosityValue.setText(response.getAirLuminosity().getValue());
        mAirLuminosityUnity.setText(response.getAirLuminosity().getUnity());

        mSoilHumidityValue.setText(response.getSoilHumidity().getValue());
        mSoilHumidityUnity.setText(response.getSoilHumidity().getUnity());

        mSoilTemperatureValue.setText(response.getSoilTemperature().getValue());
        mSoilTemperatureUnity.setText(response.getSoilTemperature().getUnity());

        mSoilCapacityValue.setText(response.getSoilCapacity().getValue());
        mSoilCapacityUnity.setText(response.getSoilCapacity().getUnityType());

        mWeatherRainfallHourValue.setText(response.getWeatherRainfall().getValuePerHour());
        mWeatherRainfallHourUnity.setText(response.getWeatherRainfall().getUnity());

        mWeatherRainfallDayValue.setText(response.getWeatherRainfall().getValuePerDay());
        mWeatherRainfallDayUnity.setText(response.getWeatherRainfall().getUnity());

        mWeatherWindDirectionValue.setText(response.getWeatherWindDirection().getValue());
        mWeatherWindDirectionDirection.setText(response.getWeatherWindDirection().getDirection());

        mWeatherWindSpeedAverageValue.setText(response.getWeatherWindSpeed().getAverageValue());
        mWeatherWindSpeedAverageUnity.setText(response.getWeatherWindSpeed().getUnity());

        mWeatherWindSpeedMaxValue.setText(response.getWeatherWindSpeed().getMaxValue());
        mWeatherWindSpeedMaxUnity.setText(response.getWeatherWindSpeed().getUnity());

    }
}