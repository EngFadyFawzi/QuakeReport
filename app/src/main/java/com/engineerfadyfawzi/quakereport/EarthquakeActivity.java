package com.engineerfadyfawzi.quakereport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
{
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    
    /**
     * URL for earthquake data from the USGS data set
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=5&limit=10";
    
    private EarthquakeAdapter mAdapter;
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.earthquake_activity );
        
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = findViewById( R.id.list_view );
        
        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter( this, new ArrayList< Earthquake >() );
        
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter( mAdapter );
        
        // set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick( AdapterView< ? > adapterView, View view, int position, long l )
            {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem( position );
                
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse( currentEarthquake.getUrl() );
                
                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent( Intent.ACTION_VIEW, earthquakeUri );
                
                // Send the intent to launch a new activity
                startActivity( websiteIntent );
            }
        } );
        
        // Start the AsyncTask to fetch the earthquakes data
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute( USGS_REQUEST_URL );
    }
    
    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of earthquakes in the response.
     *
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return an Earthquake. We won't do
     * progress updates, so the second generic is just Void.
     *
     * We'll only override two of the methods of AsyncTask: doInBackground() and onPostExecute().
     * The doInBackground() method runs on a background thread, so it can run long running code
     * (like network activity), without interfering with the responsiveness  of the app.
     * Then onPostExecute() is passed the result of doInBackground() method, but runs on the
     * UI thread (main thread), so it can use the produced data to update the UI.
     */
    private class EarthquakeAsyncTask extends AsyncTask< String, Void, List< Earthquake > >
    {
        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Earthquake}s as the result
         *
         * @param urls
         * @return
         */
        @Override
        protected List< Earthquake > doInBackground( String... urls )
        {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if ( urls.length < 1 || urls[ 0 ] == null )
                return null;
            
            // Preform the HTTP request for earthquake data and process the response.
            // Get the list of earthquakes from {@link QueryUtils}'s fetchEarthquakeData method
            List< Earthquake > result = QueryUtils.fetchEarthquakeData( urls[ 0 ] );
            
            // Return the list of {@link Earthquake}s objects as the result of the {@link EarthquakeAsyncTask}
            return result;
        }
        
        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground() method.
         *
         * First we clear out the adapter, to get rid of earthquake data from a previous query to USGS.
         * Then we update the adapter with the new list of earthquakes,
         * which will trigger the ListView to re-populate its list items.
         *
         * @param data
         */
        @Override
        protected void onPostExecute( List< Earthquake > data )
        {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();
            
            // If there is a valid list of {@link Earthquake}s, then add them to adapter's
            // data set. This will trigger the ListView to update.
            if ( data != null && !data.isEmpty() )
                mAdapter.addAll( data );
        }
    }
}