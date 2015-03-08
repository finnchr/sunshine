package com.finnchristian.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finnchristian.android.sunshine.app.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        private static final int FORECAST_LOADER_ID = 0;

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
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;

        protected String forecastStr;
        protected ShareActionProvider shareActionProvider;

        public DetailFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState){
            getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
            super.onActivityCreated(savedInstanceState);
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
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if(intent != null) {
                forecastStr = intent.getDataString();
            }

            if(forecastStr != null) {
                TextView textView = (TextView) rootView.findViewById(R.id.detail_text);
                textView.setText(forecastStr);
            }

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = Uri.parse(forecastStr);
            return new CursorLoader(getActivity(), uri, FORECAST_COLUMNS /*projection*/,
                    null /*selection*/,
                    null /*selectionArgs*/,
                    null /*sortOrder*/);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(data.moveToFirst()) {
                long date = data.getLong(COL_WEATHER_DATE);
                String desc = data.getString(COL_WEATHER_DESC);
                double max = data.getDouble(COL_WEATHER_MAX_TEMP);
                double min = data.getDouble(COL_WEATHER_MIN_TEMP);

                boolean isMetric = Utility.isMetric(getActivity());

                forecastStr = String.format("%s - %s - %s/%s", Utility.formatDate(date),
                        desc,
                        Utility.formatTemperature(getActivity(), max, isMetric),
                        Utility.formatTemperature(getActivity(), min, isMetric));

                TextView textView = (TextView) getView().findViewById(R.id.detail_text);
                textView.setText(forecastStr);

                if(shareActionProvider != null && forecastStr != null && !forecastStr.isEmpty()) {
                    shareActionProvider.setShareIntent(createShareForecastIntent());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
