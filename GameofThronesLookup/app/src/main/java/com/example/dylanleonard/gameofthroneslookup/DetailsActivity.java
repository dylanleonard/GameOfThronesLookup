package com.example.dylanleonard.gameofthroneslookup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.dylanleonard.gameofthroneslookup.MainActivity.*;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        setTitle(title);

        class DownloadTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {

                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return "Failed due to malformed URL";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Failed due to IO exception";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.i("Episode content", result);
            }
        }

        TextView yearView = (TextView) findViewById(R.id.yearView);
        TextView ratedView = (TextView) findViewById(R.id.ratedView);
        TextView releasedView = (TextView) findViewById(R.id.releasedView);
        TextView seasonView = (TextView) findViewById(R.id.seasonView);
        TextView episodeView = (TextView) findViewById(R.id.episodeView);
        TextView runtimeView = (TextView) findViewById(R.id.runtimeView);

        DownloadTask task = new DownloadTask();
        String result = null;
        JSONObject detailsJSONObject = null;
        try {
            result = task.execute(" http://www.omdbapi.com/?i="+id+"&plot=short&r=json").get();
            detailsJSONObject = new JSONObject(result);
            Log.i("Details downloaded", detailsJSONObject.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            yearView.setText(detailsJSONObject.getString("Year"));
            ratedView.setText(detailsJSONObject.getString("Rated"));
            releasedView.setText(detailsJSONObject.getString("Released"));
            seasonView.setText(detailsJSONObject.getString("Season"));
            episodeView.setText(detailsJSONObject.getString("Episode"));
            runtimeView.setText(detailsJSONObject.getString("Runtime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    }
}
