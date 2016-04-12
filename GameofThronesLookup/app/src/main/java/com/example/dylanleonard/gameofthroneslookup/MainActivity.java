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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class MainActivity extends AppCompatActivity {

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url= new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch(MalformedURLException e){
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

    public void changeActivity(View view){

        Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DownloadTask task = new DownloadTask();
        String result = null;
        JSONObject jsonObject = null;
        try {
            result = task.execute("http://www.omdbapi.com/?t=Game%20of%20Thrones&Season=1").get();
            jsonObject = new JSONObject(result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray episodeTitleJSONArr = null;
        try {
            episodeTitleJSONArr = jsonObject.getJSONArray("Episodes");
            Log.i("Array extracted", "YES");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.i("Episode Array", episodeTitleJSONArr.toString());

        Log.i("Contents of URL", result);
        if(result == "Failed")
            Log.i("Connection", "Failure");

        ListView episodeList = (ListView)findViewById(R.id.episodeList);
        final ArrayList<String> episodeTitleArr = new ArrayList<String>();
        final ArrayList<String> episodeImdbIDArr = new ArrayList<String>();



        for(int i=0; i < episodeTitleJSONArr.length(); i++) {
            try {
                episodeTitleArr.add(episodeTitleJSONArr.getJSONObject(i).getString("Title"));
                episodeImdbIDArr.add(episodeTitleJSONArr.getJSONObject(i).getString("imdbID"));
                Log.i("Episode added to list: ", episodeTitleJSONArr.getJSONObject(i).getString("Title"));
                Log.i("imdbID added to list: ", episodeTitleJSONArr.getJSONObject(i).getString("imdbID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, episodeTitleArr);

        episodeList.setAdapter(arrayAdapter);

        episodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.i("Episode tapped:", episodeTitleArr.get(position));
                Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                i.putExtra("id", episodeImdbIDArr.get(position));
                i.putExtra("title", episodeTitleArr.get(position));
                Log.i("ImdbID passed", episodeImdbIDArr.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
