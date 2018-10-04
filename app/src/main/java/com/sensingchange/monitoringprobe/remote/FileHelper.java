package com.sensingchange.monitoringprobe.remote;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileHelper {
    final static String fileName = "database.txt";
    final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    final static String TAG = FileHelper.class.getName();

    public static boolean saveToFile( String data){
        try {
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)){

                File root = new File(Environment.getExternalStorageDirectory(), "Database");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, fileName);
                FileWriter writer = new FileWriter(gpxfile, true);
                writer.append(data);
                writer.flush();
                writer.close();

                return true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
                // Read only
                return false;
            }
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return  false;

    }

    public static  String ReadFile( Context context){
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(path + fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return line;
    }

}