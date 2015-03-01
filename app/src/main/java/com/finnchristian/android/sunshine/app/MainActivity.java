package com.finnchristian.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }

        Log.d(TAG, "onCreate invoked");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart invoked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume invoked");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause invoked");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop invoked");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart invoked");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy invoked");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState invoked");
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        Log.d(TAG, "onRestoreInstanceState invoked");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        else if(id == R.id.action_show_preferred_location) {
            openPreferredLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0,0").buildUpon().appendQueryParameter("q", location).build();
        intent.setData(uri);

        // Make sure we're able to start activity before actually starting it
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
