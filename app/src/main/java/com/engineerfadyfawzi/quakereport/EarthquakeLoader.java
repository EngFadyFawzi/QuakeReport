package com.engineerfadyfawzi.quakereport;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.loader.content.AsyncTaskLoader;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 *
 * To define the EarthquakeLoader class, we extend AsyncTaskLoader and specify List as the generic
 * parameter, which explains what type of data is expected to be loaded. In this case, the loader is
 * loading a list of Earthquake objects.
 * Then we take a String URL in the constructor, and in loadInBackground(), we'll do the exact same
 * operations as in doInBackground back in EarthquakeAsyncTask.
 */
public class EarthquakeLoader extends AsyncTaskLoader< List< Earthquake > >
{
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    
    /**
     * Query URL
     */
    private String mUrl;
    
    /**
     * List of earthquakes that return from another thread.
     */
    private List< Earthquake > earthquakes;
    
    
    /**
     * Constructors a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public EarthquakeLoader( Context context, String url )
    {
        super( context );
        mUrl = url;
    }
    
    /**
     * Important: Notice that we also override the onStartLoading() method to call forceLoad()
     * which is a required step to actually trigger the loadInBackground() method to execute.
     *
     * I implicitly you can utilize this method to stop calling loadInBackground() unnecessarily by
     * using deliverResult() method which delivers the result of the previous load to the registered
     * listener's onLoadFinished() method which in turn allows us to skip loadInBackground() call.
     */
    @Override
    protected void onStartLoading()
    {
        Log.i( LOG_TAG, "TEST: onStartLoading() called ..." );
        
        if ( earthquakes != null )
            deliverResult( earthquakes ); // skip loadInBackground() call
        else
            forceLoad(); // call loadInBackground()
    }
    
    /**
     * This is on a background thread.
     *
     * @return
     */
    @Override
    public List< Earthquake > loadInBackground()
    {
        Log.i( LOG_TAG, "TEST: loadInBackground() called ..." );
        
        // Don't perform the request if the URL is null and return early
        if ( mUrl == null )
            return null;
        
        // Perform the network request, parse the response, and extract a list of earthquakes
        
        // Preform the HTTP request for earthquake data and process the response.
        // Get the list of earthquakes from {@link QueryUtils}'s fetchEarthquakeData method.
        earthquakes = QueryUtils.fetchEarthquakeData( mUrl );
        
        // Return the list of {@link Earthquake}s objects as the result of the {@link EarthquakeLoader}
        return earthquakes;
    }
}