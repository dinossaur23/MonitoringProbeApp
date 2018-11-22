package com.sensingchange.monitoringprobe;

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
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sensingchange.monitoringprobe.remote.FileHelper;

public class PairActivity extends Activity {
    Button tempButton;
    TextView myLabel;

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 64;
    final byte end_of_file = 33;
    int readBufferPosition = 0;
    public boolean finished = false;


    public void sendBtMsg(String msg){
        UUID uuid = UUID.fromString("8bacc104-15eb-4b37-bea6-0df3ac364199"); //Standard SerialPortService ID
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

    }

    public void pair_and_send(View v){
        setContentView(R.layout.activity_pair);

        myLabel = findViewById(R.id.text_window);
        tempButton = findViewById(R.id.btnPair);


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
        };


        // start temp button handler

        tempButton.setOnClickListener(new View.OnClickListener() {

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

}
