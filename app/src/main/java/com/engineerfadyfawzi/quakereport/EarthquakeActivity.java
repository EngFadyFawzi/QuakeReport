package com.engineerfadyfawzi.quakereport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks< List< Earthquake > >
{
    /**
     * Tag for log messages
     */
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    
    
    /**
     * URL for earthquake data from the USGS data set
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    
    /**
     * Adapter for the list of earthquakes
     */
    private EarthquakeAdapter mAdapter;
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        Log.i( LOG_TAG, "TEST: Earthquake Activity onCreate() called ..." );
        
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
        
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();
        
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        Log.i( LOG_TAG, "TEST: calling initLoader() ..." );
        loaderManager.initLoader( EARTHQUAKE_LOADER_ID, null, this );
    }
    
    /**
     * We need onCreateLoader(), for when the LoaderManager has determined that the loader with our
     * specified ID isn't running, so we should create a new one.
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader< List< Earthquake > > onCreateLoader( int id, Bundle args )
    {
        Log.i( LOG_TAG, "TEST: onCreateLoader() called ..." );
        
        // COMPLETED: Create a new loader for the given URL
        return new EarthquakeLoader( this, USGS_REQUEST_URL );
    }
    
    /**
     * We need onLoadFinished(), where we'll do exactly what we did in onPostExecute(),
     * and use the earthquake data to update our UI - by updating the data set in the adapter.
     *
     * @param loader
     * @param earthquakes
     */
    @Override
    public void onLoadFinished( Loader< List< Earthquake > > loader, List< Earthquake > earthquakes )
    {
        Log.i( LOG_TAG, "TEST: onLoadFinished() called ..." );
        
        // COMPLETED: Update the UI with the result
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        
        // If there is a valid list of {@link Earthquake}s, then add them to adapter's
        // data set. This will trigger the ListView to update.
        if ( earthquakes != null && !earthquakes.isEmpty() )
            mAdapter.addAll( earthquakes );
    }
    
    /**
     * We need onLoaderReset(), we're we're being informed that the data from our loader
     * is no longer valid. This isn't actually a case that's going to come up with our simple
     * loader, but the correct thing to do is to remove all the earthquake data from our UI by
     * clearing out the adapter’s data set.
     *
     * @param loader
     */
    @Override
    public void onLoaderReset( Loader< List< Earthquake > > loader )
    {
        Log.i( LOG_TAG, "TEST: onLoaderReset() called ..." );
        
        // COMPLETED: Loader reset, so we can clear out our existing data.
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
    }
}