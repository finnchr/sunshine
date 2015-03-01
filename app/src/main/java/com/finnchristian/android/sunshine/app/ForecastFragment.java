package com.finnchristian.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by finnchr on 14.02.2015.
 */
public class ForecastFragment extends Fragment {
    private static final String TAG = ForecastFragment.class.getSimpleName();

    protected ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeather();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> weekForecast = new ArrayList<>();

        FragmentActivity activity = getActivity();

        forecastAdapter = new ArrayAdapter<>(
                // The current context (this fragment's parent activity)
                activity,
                // ID of list item layout
                R.layout.list_item_forecast,
                // ID of the textview to populate
                R.id.list_item_forecast_textview,
                // Forecast data
                weekForecast);

        final ListView forecaseListView = (ListView)rootView.findViewById(R.id.listview_forecast);
        forecaseListView.setAdapter(forecastAdapter);

        forecaseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String forecast = (String) forecaseListView.getItemAtPosition(position);
                String forecast = forecastAdapter.getItem(position);

                //Toast toast = Toast.makeText(getActivity(), forecast, Toast.LENGTH_LONG);
                //toast.show();

                Bundle parameters = new Bundle();
                parameters.putString("forecast", forecast);

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtras(parameters);
                detailIntent.putExtra(Intent.EXTRA_TEXT, forecast);

                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    private void updateWeather() {
        String locationKey = getString(R.string.pref_location_key);
        String defaultLocation = getString(R.string.pref_location_default);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = pref.getString(locationKey, defaultLocation);

        FetchWeatherTask task = new FetchWeatherTask(getActivity(), forecastAdapter);
        //task.execute("94043");
        task.execute(location);
    }

}
