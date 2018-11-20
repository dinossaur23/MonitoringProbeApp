package com.sensingchange.monitoringprobe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sensingchange.monitoringprobe.model.graph.AirData;
import com.sensingchange.monitoringprobe.model.graph.Registers;
import com.sensingchange.monitoringprobe.model.graph.SoilData;
import com.sensingchange.monitoringprobe.remote.GraphicService;
import com.sensingchange.monitoringprobe.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GraphicsActivity extends AppCompatActivity {
    SoilData soil_data;
    AirData air_data;
    Button btnLastDay;
    Button btnLastWeek;
    Button btnLastMonth;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        btnLastDay = findViewById(R.id.btnLastDay);
        btnLastWeek = findViewById(R.id.btnLastWeek);
        btnLastMonth = findViewById(R.id.btnLastMonth);
        btnBack = findViewById(R.id.btnBack);

        btnLastDay.setEnabled(false);
      //  btnLastDay.setBackgroundTintList(getResources().getColorStateList(R.color.colorGray));
        getSoilData("last_month");
        getAirData("last_month");

        btnLastWeek.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(GraphicsActivity.this, "Week", Toast.LENGTH_SHORT).show();
                btnLastWeek.setEnabled(false);
               // btnLastWeek.setBackgroundTintList(getResources().getColorStateList(R.color.colorGray));
                getSoilData("last_week");
                getAirData("last_week");
                btnLastDay.setEnabled(true);
               // btnLastDay.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
                btnLastMonth.setEnabled(true);
               // btnLastMonth.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
            }
        });

//        btnLastMonth.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                btnLastMonth.setEnabled(false);
//               // btnLastMonth.setBackgroundTintList(getResources().getColorStateList(R.color.colorGray));
//                getSoilData("last_month");
//                getAirData("last_month");
//                btnLastDay.setEnabled(true);
//              //  btnLastDay.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
//                btnLastWeek.setEnabled(true);
//              //  btnLastWeek.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
//            }
//        });
//
//        btnLastDay.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                btnLastDay.setEnabled(false);
//               // btnLastDay.setBackgroundTintList(getResources().getColorStateList(R.color.colorGray));
//                getSoilData("last_day");
//                getAirData("last_day");
//                btnLastWeek.setEnabled(true);
//               // btnLastWeek.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
//                btnLastMonth.setEnabled(true);
//              //  btnLastMonth.setBackgroundTintList(getResources().getColorStateList(R.color.colorPink));
//            }
//        });


    }

    private void getSoilData(String period){
        /*Create handle for the RetrofitClient interface*/
        GraphicService service = RetrofitClient.getClient().create(GraphicService.class);

        /*Call the method with parameter in the interface to get the user login*/
        Call<SoilData> call = service.getSoilData("application/json", period);

        call.enqueue(new Callback<SoilData>() {

            @Override
            public void onResponse(Call<SoilData> call, Response<SoilData> response) {
                Integer status = response.code();
                if (status.equals(200)) {
                    Toast.makeText(GraphicsActivity.this, "Request", Toast.LENGTH_SHORT).show();
                    soil_data = response.body();
                    setContentView(R.layout.activity_graphics);

                    // Soil Humidity Chart
                    LineChart lineSoilHumidityChart = findViewById(R.id.soil_humidity_chart);
                    lineSoilHumidityChart.getDescription().setText("hours");
                    List <Registers> soil_humidity = soil_data.getHumidities();

                    final int soil_humidity_size = soil_humidity.size();
                    ArrayList <Entry> soil_humidity_entries = new ArrayList<>();
                    final ArrayList <String> soil_humidity_labels = new ArrayList<>();

                    for (int i = 0; i < soil_humidity_size; i++) {
                        String soil_day = soil_humidity.get(i).getDay();
                        String soil_hour = soil_humidity.get(i).getHour();
                        Float soil_value = Float.valueOf(soil_humidity.get(i).getValue());

                        soil_humidity_entries.add(new Entry(i, soil_value));
                        soil_humidity_labels.add(soil_day+"d"+soil_hour+"h");
                    }

                    LineDataSet soil_humidity_dataset = new LineDataSet(soil_humidity_entries, "# unity");
                    soil_humidity_dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    soil_humidity_dataset.setDrawFilled(true);
                    soil_humidity_dataset.setFillColor(getResources().getColor(R.color.colorAccent));
                    soil_humidity_dataset.setColor(getResources().getColor(R.color.colorAccent));
                    soil_humidity_dataset.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

                    LineData data_soil_humidity = new LineData(soil_humidity_dataset);

                    XAxis xAxisSH = lineSoilHumidityChart.getXAxis();
                    xAxisSH.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisSH.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return soil_humidity_labels.get((int)value);
                        }
                    });

                    lineSoilHumidityChart.setData(data_soil_humidity);

                    // Soil Temperature Chart
                    LineChart lineSoilTemperatureChart = findViewById(R.id.soil_temperature_chart);
                    lineSoilTemperatureChart.getDescription().setText("hours");
                    List <Registers> soil_temperature = soil_data.getTemperatures();

                    final int soil_temperature_size = soil_temperature.size();
                    ArrayList <Entry> soil_temperature_entries = new ArrayList<>();
                    final ArrayList <String> soil_temperature_labels = new ArrayList<>();

                    for (int i = 0; i < soil_temperature_size; i++) {
                        String soil_day = soil_temperature.get(i).getDay();
                        String soil_hour = soil_temperature.get(i).getHour();
                        Float soil_value = Float.valueOf(soil_temperature.get(i).getValue());

                        soil_temperature_entries.add(new Entry(i, soil_value));
                        soil_temperature_labels.add(soil_day+"d"+soil_hour+"h");
                    }

                    LineDataSet soil_temperature_dataset = new LineDataSet(soil_temperature_entries, "# Cº");
                    soil_temperature_dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    soil_temperature_dataset.setDrawFilled(true);
                    soil_temperature_dataset.setFillColor(getResources().getColor(R.color.colorAccent));
                    soil_temperature_dataset.setColor(getResources().getColor(R.color.colorAccent));
                    soil_temperature_dataset.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

                    LineData data_soil_temperature = new LineData(soil_temperature_dataset);

                    XAxis xAxisST = lineSoilTemperatureChart.getXAxis();
                    xAxisST.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisST.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return soil_temperature_labels.get((int)value);
                        }
                    });

                    lineSoilTemperatureChart.setData(data_soil_temperature);

                    // Soil Capacity Chart
                    LineChart lineSoilCapacityChart = findViewById(R.id.soil_capacity_chart);
                    lineSoilCapacityChart.getDescription().setText("hours");
                    List <Registers> soil_capacity = soil_data.getCapacities();

                    final int size = soil_capacity.size();
                    ArrayList <Entry> soil_capacity_entries = new ArrayList<>();
                    final ArrayList <String> soil_capacity_labels = new ArrayList<>();

                    for (int i = 0; i < size; i++) {
                        String soil_day = soil_capacity.get(i).getDay();
                        String soil_hour = soil_capacity.get(i).getHour();
                        Float soil_value = Float.valueOf(soil_capacity.get(i).getValue());

                        soil_capacity_entries.add(new Entry(i, soil_value));
                        soil_capacity_labels.add(soil_day+"d"+soil_hour+"h");
                    }

                    LineDataSet soil_capacity_dataset = new LineDataSet(soil_capacity_entries, "# unity");
                    soil_capacity_dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    soil_capacity_dataset.setDrawFilled(true);
                    soil_capacity_dataset.setFillColor(getResources().getColor(R.color.colorAccent));
                    soil_capacity_dataset.setColor(getResources().getColor(R.color.colorAccent));
                    soil_capacity_dataset.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

                    LineData data_soil_capacity = new LineData(soil_capacity_dataset);

                    XAxis xAxisSC = lineSoilCapacityChart.getXAxis();
                    xAxisSC.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisSC.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return soil_capacity_labels.get((int)value);
                        }
                    });

                    lineSoilCapacityChart.setData(data_soil_capacity);

                } else if (status.equals(401)) {
                    Toast.makeText(GraphicsActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GraphicsActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<SoilData> call, Throwable t) {
                Context context = GraphicsActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(t.toString());
                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getAirData(String period){
        /*Create handle for the RetrofitClient interface*/
        GraphicService service = RetrofitClient.getClient().create(GraphicService.class);

        /*Call the method with parameter in the interface to get the user login*/
        Call<AirData> call = service.getAirData("application/json", period);

        call.enqueue(new Callback<AirData>() {

            @Override
            public void onResponse(Call<AirData> call, Response<AirData> response) {
                Integer status = response.code();
                if (status.equals(200)) {
                    air_data = response.body();

                } else if (status.equals(401)) {
                    Toast.makeText(GraphicsActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GraphicsActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<AirData> call, Throwable t) {
                Context context = GraphicsActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(t.toString());
                Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
