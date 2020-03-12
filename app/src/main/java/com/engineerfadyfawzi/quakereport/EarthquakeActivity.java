package com.engineerfadyfawzi.quakereport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderCallbacks< List< Earthquake > >
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
        super.onCreate( savedInstanceState );
        setContentView( R.layout.earthquake_activity );
        
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = findViewById( R.id.list_view );
        
        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter( this, new ArrayList< Earthquake >() );
        
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter( mAdapter );
        
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();
        
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
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
        // COMPLETED: Update the UI with the result
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        
        // If there is a valid list of {@link Earthquake}s, then add them to the adapters's
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
        // COMPLETED: Loader reset, so we can clear out our existing data.
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
    }
}