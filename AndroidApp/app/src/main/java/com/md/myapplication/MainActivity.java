package com.md.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( isOnline() ){
                    new GetURLData().execute("https://yesno.wtf/api");
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Нет соединения с интернетом!",Toast.LENGTH_LONG).show();
                }
            }
            });
    }

    private class GetURLData extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader reader = null;
            URL url = null;
            HttpsURLConnection connection = null;
            String imageURL = "";
            try {
                url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                InputStream responseStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(responseStream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                String json = "";

                while ((line = reader.readLine()) != null)
                    json = buffer.append(line).append("\n").toString();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    imageURL = jsonObject.getString("image");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return imageURL;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String imageUrl) {
            super.onPostExecute(imageUrl);
            Glide.with(imageView).load(imageUrl).into(imageView);
        }

    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else { return true; }
    }

}