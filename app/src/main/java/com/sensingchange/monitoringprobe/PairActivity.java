package com.sensingchange.monitoringprobe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sensingchange.monitoringprobe.model.measurement.Post;
import com.sensingchange.monitoringprobe.remote.FileHelper;
import com.sensingchange.monitoringprobe.remote.MeasurementService;
import com.sensingchange.monitoringprobe.remote.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PairActivity extends Activity {
    Button btnPair;
    Button btnSend;
    TextView send_info;

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 64;
    final byte end_of_file = 33;
    int readBufferPosition = 0;
    public boolean finished = false;
    public boolean sended_to_server = false;
    String database_path = "Database/database.txt";
    String body_utf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);
        btnPair = findViewById(R.id.btnPair);
        send_info = findViewById(R.id.send_info);
        btnSend = findViewById(R.id.btnSend);

        if (has_collected_data()){
            send_info.setTextColor(getResources().getColor(R.color.colorGreen));
            send_info.setText(R.string.send_info_collect);
        }
        else {
            btnSend.setEnabled(false);
        }
    }

    private boolean has_collected_data(){
        File file = new File(Environment.getExternalStorageDirectory(), database_path);
        if(file.exists()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void back(View v){
        Intent intent = new Intent(PairActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void send_to_server(View v){
        if (has_collected_data()){
            ConvertUTF8();
            String converted[] = body_utf.split("\\r?\\n");
            for (int i = 0; i < converted.length; i++) {
               post_measurement(converted[i], i);
            }

            if(!sended_to_server) {
                Toast.makeText(PairActivity.this, "Sent!", Toast.LENGTH_SHORT).show();
                btnSend.setEnabled(false);
                send_info.setTextColor(getResources().getColor(R.color.colorRed));
                send_info.setText(getResources().getString(R.string.send_info));
                FileHelper.DeleteDatabaseFile();
            }
        }
    }

    private void post_measurement(String body, final Integer i){
        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(body);

        /*Create handle for the RetrofitClient interface*/
        MeasurementService service = RetrofitClient.getClient().create(MeasurementService.class);

        /*Call the method with parameter in the interface to get the user login*/
        Call<Post> call = service.postMeasurement("application/json", element);

        call.enqueue(new Callback<Post>() {

            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Integer status = response.code();
                if (status.equals(200)) {
                    Toast.makeText(PairActivity.this, "Sending " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                    sended_to_server = true;
                } else if (status.equals(401)) {
                    Toast.makeText(PairActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PairActivity.this, "Unknown error", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(PairActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendBtMsg(String msg){
        UUID uuid = UUID.fromString(getResources().getString(R.string.raspberry_bluetooth_key)); //Standard SerialPortService ID
        try {

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()){
                mmSocket.connect();
            }

            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void pair_and_send(View v){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final class workerThread implements Runnable {

            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run()
            {
                while(!finished){
                    sendBtMsg(btMsg);
                    while(!Thread.currentThread().isInterrupted()) {
                        int bytesAvailable;
                        boolean workDone = false;

                        try {

                            final InputStream mmInputStream;
                            mmInputStream = mmSocket.getInputStream();
                            bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                byte[] readBuffer = new byte[1000];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(packetBytes, 0, encodedBytes, 0, packetBytes.length - 1);
                                        final String data = new String(encodedBytes, "UTF-8");
                                        SaveInFile(data);

                                        readBufferPosition = 0;

                                        workDone = true;
                                        break;


                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                    if (b == end_of_file) {
                                        workDone = true;
                                        finished = true;
                                        break;
                                    }
                                }

                                if (workDone) {
                                    mmSocket.close();
                                    break;
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }


        // start temp button handler

        btnPair.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Perform action on temp button click

                (new Thread(new workerThread("temp"))).start();

            }
        });


        //end temp button handler

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Toast.makeText(PairActivity.this, "Monitoring: " + device.getName(), Toast.LENGTH_SHORT).show();
                    //Log.e("Monitoring",device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }
        if (mmDevice == null) {
            Toast.makeText(PairActivity.this, "Raspberry not found!", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(PairActivity.this, HomeActivity.class);
//            startActivity(intent);
        }
    }

    private void SaveInFile(String response){
        try {
            FileHelper.saveToFile(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ConvertUTF8(){
        try {
            body_utf = FileHelper.ConvertUTF8();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
