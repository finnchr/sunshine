package com.finnchristian.android.sunshine.app;

import android.content.Intent;
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
    public static class DetailFragment extends Fragment {
        private static final String TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

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
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share_weather);
            //shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
            shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if(shareActionProvider != null) {
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
            //String forecast = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
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
    }
}
