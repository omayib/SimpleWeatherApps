package id.technomotion.simpleweatherapps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    OkHttpClient client;
    TextView textViewTemp, textViewDesc;
    ImageView imageViewIcon;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();

        textViewDesc = findViewById(R.id.textViewDescription);
        textViewTemp = findViewById(R.id.textViewTemp);
        imageViewIcon = findViewById(R.id.imageViewIcon);
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                gettingTheData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


    }

    private void gettingTheData(final String queryCity) {
        final String path = "http://api.openweathermap.org/data/2.5/weather?q=%s,ID&units=metric&APPID=d714508e67bb45e1804ab1e7d534e7c3";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String apiResponse = getData(String.format(path,queryCity));
                    System.out.println(apiResponse);
                    processTheResponse(apiResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processTheResponse(String apiResponse) {
        try {
            JSONObject responseObject = new JSONObject(apiResponse);
            JSONObject weatherObject = responseObject.getJSONArray("weather").getJSONObject(0);
            String description = weatherObject.getString("description");
            String icon = weatherObject.getString("icon");
            Double temp = responseObject.getJSONObject("main").getDouble("temp");
            System.out.println("======");
            System.out.println(description);
            System.out.println(icon);
            System.out.println(temp);
            displayTheData(temp, description, icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayTheData(final Double temp, final String description, final String icon) {
        final String imagePath = "http://openweathermap.org/img/w/%s.png";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewDesc.setText(description);
                textViewTemp.setText(String.valueOf(Math.round(temp)));
                System.out.println(String.format(imagePath,icon));
                Picasso.with(MainActivity.this).load(String.format(imagePath,icon)).into(imageViewIcon);
            }
        });
    }


    String getData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
