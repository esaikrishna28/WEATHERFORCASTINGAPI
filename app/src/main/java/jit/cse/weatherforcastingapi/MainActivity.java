package jit.cse.weatherforcastingapi;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    TextView cityname,temperature,tvresults,climatedis;

EditText etcity;
ImageButton searchbtn;
ImageView climateimg;
    LocationManager locationManager;
    String latitude, longitude;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "7e0f6f35c89615946c3363793a3ab42f";
    DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etcity=findViewById(R.id.etcity);
        cityname=findViewById(R.id.cityname);
        searchbtn=findViewById(R.id.btnsearch);
        climateimg=findViewById(R.id.climateimg);
        climatedis=findViewById(R.id.climatedis);
        temperature=findViewById(R.id.temperature);
        tvresults=findViewById(R.id.tvResults);

        ActivityCompat.requestPermissions( this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
      //  getlatandlong();
        updatelivedata();
    }

    private void getlatandlong() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }


    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
               // showLocation.setText("Your Location: " + " " + "Latitude: " + latitude + " " + "Longitude: " + longitude);
                Log.d(TAG, "getLocation:LATITUDE "+latitude+" LONGITUDE" +longitude);

               // updatelivedata();


            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatelivedata() {

        String tempurl1="";
        tempurl1 = url + "?q=Bengaluru" + "&appid=" + appid;
        //tempurl1=url+"?lat="+latitude+"&lon="+longitude+"&appid="+appid;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, tempurl1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                String op1 = "";
                String op2 = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    String descriptionhead=jsonObjectWeather.getString("main");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    output += "\n\nCurrent weather of " + cityName + " (" + countryName + ")"
//                                + "\n Temp: " + df.format(temp) + " °C"
                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
                            + "\n Humidity: " + humidity + "%"
//                                + "\n Description: " + description
                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";
                    op1="Temp: " + df.format(temp) + " °C";
                    switch (descriptionhead){
                        case "Rain":climateimg.setImageResource(R.drawable.rain);break;
                        case "Atmosphere":climateimg.setImageResource(R.drawable.atmosphere);break;
                        case "Haze":climateimg.setImageResource(R.drawable.atmosphere);break;
                        case "Clear":climateimg.setImageResource(R.drawable.clear);break;
                        case "Clouds":climateimg.setImageResource(R.drawable.clouds);break;
                        case "Snow":climateimg.setImageResource(R.drawable.snow);break;
                        case "Thunderstorm":climateimg.setImageResource(R.drawable.thunderstorm);break;
                        case "Drizzle":climateimg.setImageResource(R.drawable.drizzle);break;
                        default:climateimg.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                    cityname.setText(cityName);
                    temperature.setText(op1);
                    tvresults.setText(output);
                    climatedis.setText(description);
                    Log.d(TAG, descriptionhead);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "INVALID CITY", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);



    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = etcity.getText().toString().trim();
        if(city.equals("")){
            tvresults.setText("City field can not be empty!");
        }else{
            if(!city.equals("")){
                tempUrl = url + "?q=" + city + "&appid=" + appid;
            }
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    String op1 = "";
                    String op2 = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        String descriptionhead=jsonObjectWeather.getString("main");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        output += "\n\nCurrent weather of " + cityName + " (" + countryName + ")"
//                                + "\n Temp: " + df.format(temp) + " °C"
                                + "\n Feels Like: " + df.format(feelsLike) + " °C"
                                + "\n Humidity: " + humidity + "%"
//                                + "\n Description: " + description
                                + "\n Wind Speed: " + wind + "m/s (meters per second)"
                                + "\n Cloudiness: " + clouds + "%"
                                + "\n Pressure: " + pressure + " hPa";
                        op1="Temp: " + df.format(temp) + " °C";
                        switch (descriptionhead){
                            case "Rain":climateimg.setImageResource(R.drawable.rain);break;
                            case "Atmosphere":climateimg.setImageResource(R.drawable.atmosphere);break;
                            case "Haze":climateimg.setImageResource(R.drawable.atmosphere);break;
                            case "Clear":climateimg.setImageResource(R.drawable.clear);break;
                            case "Clouds":climateimg.setImageResource(R.drawable.clouds);break;
                            case "Snow":climateimg.setImageResource(R.drawable.snow);break;
                            case "Thunderstorm":climateimg.setImageResource(R.drawable.thunderstorm);break;
                            case "Drizzle":climateimg.setImageResource(R.drawable.drizzle);break;
                            default:climateimg.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                        cityname.setText(cityName);
                        temperature.setText(op1);
                        tvresults.setText(output);
                        climatedis.setText(description);
                        Log.d(TAG, descriptionhead);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "INVALID CITY", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}




