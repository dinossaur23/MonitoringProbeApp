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
import android.widget.TextView;
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
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        btnLastDay = findViewById(R.id.btnLastDay);
        btnLastWeek = findViewById(R.id.btnLastWeek);
        btnLastMonth = findViewById(R.id.btnLastMonth);
        btnBack = findViewById(R.id.btnBack);
        title = findViewById(R.id.title);

        btnLastDay.setEnabled(false);
        getSoilData("last_day");
        getAirData("last_day");

    }

    public void back(View v){
        Intent intent = new Intent(GraphicsActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void refreshLastDay(View v){
        title.setText(R.string.last_day_measurements);
        btnLastDay.setEnabled(false);
        getSoilData("last_day");
        getAirData("last_day");
        btnLastWeek.setEnabled(true);
        btnLastMonth.setEnabled(true);
    }

    public void refreshLastWeek(View v){
        title.setText(R.string.last_week_measurements);
        btnLastWeek.setEnabled(false);
        getSoilData("last_week");
        getAirData("last_week");
        btnLastDay.setEnabled(true);
        btnLastMonth.setEnabled(true);
    }

    public void refreshLastMonth(View v){
        title.setText(R.string.last_month_measurements);
        btnLastMonth.setEnabled(false);
        getSoilData("last_month");
        getAirData("last_month");
        btnLastDay.setEnabled(true);
        btnLastWeek.setEnabled(true);
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
                    soil_data = response.body();

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
                    lineSoilHumidityChart.invalidate();

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
                    lineSoilTemperatureChart.invalidate();

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
                    lineSoilCapacityChart.invalidate();

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

                    // Air Humidity Chart
                    LineChart lineAirHumidityChart = findViewById(R.id.air_humidity_chart);
                    lineAirHumidityChart.getDescription().setText("hours");
                    List <Registers> air_humidity = air_data.getHumidities();

                    final int air_humidity_size = air_humidity.size();
                    ArrayList <Entry> air_humidity_entries = new ArrayList<>();
                    final ArrayList <String> air_humidity_labels = new ArrayList<>();

                    for (int i = 0; i < air_humidity_size; i++) {
                        String air_day = air_humidity.get(i).getDay();
                        String air_hour = air_humidity.get(i).getHour();
                        Float air_value = Float.valueOf(air_humidity.get(i).getValue());

                        air_humidity_entries.add(new Entry(i, air_value));
                        air_humidity_labels.add(air_day+"d"+air_hour+"h");
                    }

                    LineDataSet air_humidity_dataset = new LineDataSet(air_humidity_entries, "# unity");
                    air_humidity_dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    air_humidity_dataset.setDrawFilled(true);
                    air_humidity_dataset.setFillColor(getResources().getColor(R.color.colorAccent));
                    air_humidity_dataset.setColor(getResources().getColor(R.color.colorAccent));
                    air_humidity_dataset.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

                    LineData data_soil_humidity = new LineData(air_humidity_dataset);

                    XAxis xAxisAH = lineAirHumidityChart.getXAxis();
                    xAxisAH.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisAH.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return air_humidity_labels.get((int)value);
                        }
                    });

                    lineAirHumidityChart.setData(data_soil_humidity);
                    lineAirHumidityChart.invalidate();

                    // Air Temperature Chart
                    LineChart lineAirTemperatureChart = findViewById(R.id.air_temperature_chart);
                    lineAirTemperatureChart.getDescription().setText("hours");
                    List <Registers> air_temperature = air_data.getTemperatures();

                    final int air_temperature_size = air_temperature.size();
                    ArrayList <Entry> air_temperature_entries = new ArrayList<>();
                    final ArrayList <String> air_temperature_labels = new ArrayList<>();

                    for (int i = 0; i < air_temperature_size; i++) {
                        String air_day = air_temperature.get(i).getDay();
                        String air_hour = air_temperature.get(i).getHour();
                        Float air_value = Float.valueOf(air_temperature.get(i).getValue());

                        air_temperature_entries.add(new Entry(i, air_value));
                        air_temperature_labels.add(air_day+"d"+air_hour+"h");
                    }

                    LineDataSet air_temperature_dataset = new LineDataSet(air_temperature_entries, "# Cº");
                    air_temperature_dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    air_temperature_dataset.setDrawFilled(true);
                    air_temperature_dataset.setFillColor(getResources().getColor(R.color.colorAccent));
                    air_temperature_dataset.setColor(getResources().getColor(R.color.colorAccent));
                    air_temperature_dataset.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

                    LineData data_soil_temperature = new LineData(air_temperature_dataset);

                    XAxis xAxisAT = lineAirTemperatureChart.getXAxis();
                    xAxisAT.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisAT.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return air_temperature_labels.get((int)value);
                        }
                    });

                    lineAirTemperatureChart.setData(data_soil_temperature);
                    lineAirTemperatureChart.invalidate();

                    // Air Luminosity Chart
                    LineChart lineAirLuminosityChart = findViewById(R.id.air_luminosity_chart);
                    lineAirLuminosityChart.getDescription().setText("hours");
                    List <Registers> air_luminosity = air_data.getLuminosities();

                    final int size = air_luminosity.size();
                    ArrayList <Entry> air_luminosity_entries = new ArrayList<>();
                    final ArrayList <String> air_luminosity_labels = new ArrayList<>();

                    for (int i = 0; i < size; i++) {
                        String air_day = air_luminosity.get(i).getDay();
                        String air_hour = air_luminosity.get(i).getHour();
                        Float air_value = Float.valueOf(air_luminosity.get(i).getValue());

                        air_luminosity_entries.add(new Entry(i, air_value));
                        air_luminosity_labels.add(air_day+"d"+air_hour+"h");
                    }

                    LineDataSet air_luminosity_dataset = new LineDataSet(air_luminosity_entries, "# unity");
                    air_luminosity_dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    air_luminosity_dataset.setDrawFilled(true);
                    air_luminosity_dataset.setFillColor(getResources().getColor(R.color.colorAccent));
                    air_luminosity_dataset.setColor(getResources().getColor(R.color.colorAccent));
                    air_luminosity_dataset.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

                    LineData data_soil_capacity = new LineData(air_luminosity_dataset);

                    XAxis xAxisAL = lineAirLuminosityChart.getXAxis();
                    xAxisAL.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxisAL.setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return air_luminosity_labels.get((int)value);
                        }
                    });

                    lineAirLuminosityChart.setData(data_soil_capacity);
                    lineAirLuminosityChart.invalidate();

                } else if (status.equals(401)) {
                    Toast.makeText(GraphicsActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GraphicsActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<AirData> call, Throwable t) {
                Context context = GraphicsActivity.this;
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

}
