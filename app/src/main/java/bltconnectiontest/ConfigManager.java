package bltconnectiontest;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by cernav1 on 25.3.2018.
 */

public class ConfigManager {
    private final String FILE_NAME = "configuration.dat";

    public ConfigManager() {
    }

    public Config getConfig(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);

        //if file does not exist, return null
        if (file == null) {
            return null;
            // read file to config structure and return
        } else {
            String path = file.getAbsolutePath();
            Config config = new Config();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
                String line = bufferedReader.readLine();
                JsonSerializer serializer = new JsonSerializer();
                config = (Config) serializer.DeserializeFromJson(line, Config.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config;
        }
    }

    public void setConfig(Context context, Config config) {
        JsonSerializer serializer = new JsonSerializer();
        String configSerialized = serializer.SerializeToJson(config);
        if (!Arrays.asList(context.fileList()).contains(FILE_NAME)) {
            new File(context.getFilesDir(), FILE_NAME);
        }

        //save content to file in internal storage
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(configSerialized.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
