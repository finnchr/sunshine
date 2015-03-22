package com.finnchristian.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.finnchristian.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private static final int DETAIL_LOADER_ID = 0;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND_SPEED = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;

    protected String forecastStr;
    protected ShareActionProvider shareActionProvider;
    private TextView dayView;
    private TextView dateView;
    private TextView highView;
    private TextView lowView;
    private ImageView detailIcon;
    private TextView forecastView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;

    private Uri uri;


    public static DetailFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable("Uri", uri);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public DetailFragment() {
    }

    public Uri getUri() {
        Bundle args = getArguments();
        if(args != null) {
            return args.getParcelable("Uri");
        }
        else {
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    void onLocationChanged(String newLocation) {
        if(uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share_weather);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(shareActionProvider != null && forecastStr != null && !forecastStr.isEmpty()) {
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else {
            Log.d(TAG, "Share action provider is null ...");
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_share_weather) {
        }

        return super.onOptionsItemSelected(item);
    }

    /** Defines a default (dummy) share intent to initialize the action provider.
     * However, as soon as the actual content to be used in the intent
     * is known or changes, you must update the share intent by again calling
     * mShareActionProvider.setShareIntent()
     */
    private Intent createShareForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, String.format("%s %s", forecastStr, FORECAST_SHARE_HASHTAG));

        return intent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        uri = getUri();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        dayView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        dateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        highView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        lowView = (TextView) rootView.findViewById(R.id.detail_low_textview);

        detailIcon = (ImageView) rootView.findViewById(R.id.detail_icon);
        forecastView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);

        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(uri != null) {
            return new CursorLoader(getActivity(), uri, FORECAST_COLUMNS /*projection*/,
                    null /*selection*/,
                    null /*selectionArgs*/,
                    null /*sortOrder*/);
        }
        else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            int weatherId = data.getInt(COL_WEATHER_ID);
            long date = data.getLong(COL_WEATHER_DATE);
            String desc = data.getString(COL_WEATHER_DESC);
            double max = data.getDouble(COL_WEATHER_MAX_TEMP);
            double min = data.getDouble(COL_WEATHER_MIN_TEMP);
            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
            float degrees = data.getFloat(COL_WEATHER_DEGREES);
            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            int weatherConditionId = data.getInt(COL_WEATHER_CONDITION_ID);

            boolean isMetric = Utility.isMetric(getActivity());

            forecastStr = String.format("%s - %s - %s/%s", Utility.formatDate(date),
                    desc,
                    Utility.formatTemperature(getActivity(), max, isMetric),
                    Utility.formatTemperature(getActivity(), min, isMetric));

            dayView.setText(Utility.getDayName(getActivity(), date));
            dateView.setText(Utility.formatDate(date));
            highView.setText(Utility.formatTemperature(getActivity(), max, isMetric));
            lowView.setText(Utility.formatTemperature(getActivity(), min, isMetric));

            detailIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherConditionId));
            forecastView.setText(desc);

            humidityView.setText(Utility.getFormattedHumidity(getActivity(), humidity));
            windView.setText(Utility.getFormattedWind(getActivity(), windSpeed, degrees));
            pressureView.setText(Utility.getFormattedPressure(getActivity(), pressure));

            if(shareActionProvider != null && forecastStr != null && !forecastStr.isEmpty()) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}