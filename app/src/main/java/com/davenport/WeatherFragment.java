package com.davenport;

import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView minTemperatureField;
    TextView maxTemperatureField;
    TextView weatherIcon;
    final String DEGREE  = "\u00b0";

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        //maxTemperatureField = (TextView)rootView.findViewById(R.id.max_temperature_field);
        //minTemperatureField = (TextView)rootView.findViewById(R.id.min_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }
     private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }
    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");


            currentTemperatureField.setText(
                    String.format(Locale.US,"%.2f", main.getDouble("temp"))+" " + DEGREE +"F");
            minTemperatureField.setText(
                    String.format(Locale.US, "%.2f", main.getDouble("temp_min"))+" "+ DEGREE + "F");
            maxTemperatureField.setText(
                    String.format(Locale.US, "%.2f", main.getDouble("temp_max"))+" "+DEGREE+ "F");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        //actual id = 900
        int id = 0;
        String icon = "";
        if(actualId >= 900){
            id=actualId;
        }
        else {
            id = actualId / 100;
        }
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                //thunder storms are always cod: 200s
                case 2 : icon = getActivity().getString(R.string.wi_thunderstorm);
                    break;
                //drizzle are always cod : 300s
                case 3 : icon = getActivity().getString(R.string.wi_showers);
                    break;
                //rain is always cod : 500s
                case 5 : icon = getActivity().getString(R.string.wi_rain);
                    break;
                //snow is always cod : 600s
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                //atmosphere is always cod : 700s
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                //extreme weather : 900s
                case 900 : icon = getActivity().getString(R.string.wi_tornado);
                    break;
                case 901 : icon = getActivity().getString(R.string.wi_thunderstorm);
                    break;
                case 902 : icon = getActivity().getString(R.string.wi_hurricane);
                    break;
                case 903 : icon = getActivity().getString(R.string.wi_snowflake_cold);
                    break;
                case 904 : icon = getActivity().getString(R.string.wi_day_sunny);
                    break;
                case 905 : icon = getActivity().getString(R.string.wi_strong_wind);
                    break;
                case 906 : icon = getActivity().getString(R.string.wi_hail);
                    break;
                case 951 : icon = getActivity().getString(R.string.wi_day_sunny);
                    break;
                case 952 : icon = getActivity().getString(R.string.wi_windy);
                    break;
                case 953 : icon = getActivity().getString(R.string.wi_windy);
                    break;
                case 954 : icon = getActivity().getString(R.string.wi_windy);
                    break;
                case 955 : icon = getActivity().getString(R.string.wi_windy);
                    break;
                case 956 : icon = getActivity().getString(R.string.wi_strong_wind);
                    break;
                case 957 : icon = getActivity().getString(R.string.wi_gale_warning);
                    break;
                case 958 : icon = getActivity().getString(R.string.wi_gale_warning);
                    break;
                case 959 : icon = getActivity().getString(R.string.wi_gale_warning);
                    break;
                case 960 : icon = getActivity().getString(R.string.wi_thunderstorm);
                    break;
                case 961 : icon = getActivity().getString(R.string.wi_storm_warning);
                    break;
                case 962 : icon = getActivity().getString(R.string.wi_hurricane);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
    public void changeCity(String city){
        updateWeatherData(city);
    }

}
