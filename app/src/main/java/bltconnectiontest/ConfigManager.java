package bltconnectiontest;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;

/**
 * Created by cernav1 on 25.3.2018.
 */

public class ConfigManager {
    private final String FILE_NAME = "configuration.dat";

    public ConfigManager() {

    }

    public void setConfig(Context context, Config config) {
        Gson gson = new Gson();

        //if file does not exist, create new one
        String configSerialized = gson.toJson(config);
        if (!Arrays.asList(context.fileList()).contains(FILE_NAME)){
            new File(context.getFilesDir(), FILE_NAME);
        }

        //save content to file in internal storage
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(configSerialized.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Config getConfig (Context context){
        File file = new File(context.getFilesDir(), FILE_NAME);

        //if file does not exist, return null
        if (file == null) {
            return null;
        // read file to config structure and return
        } else {
            String path = file.getAbsolutePath();
            Config config = new Config();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
                Gson gson = new Gson();
                config = gson.fromJson(bufferedReader, Config.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return config;
        }
    }
}
