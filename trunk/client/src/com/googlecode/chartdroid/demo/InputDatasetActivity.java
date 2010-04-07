package com.googlecode.chartdroid.demo;

import com.googlecode.chartdroid.demo.provider.DatabaseStoredData;
import com.googlecode.chartdroid.demo.provider.LocalStorageContentProvider;

import org.achartengine.demo.data.TimelineData;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InputDatasetActivity extends ListActivity {


	static final String TAG = "ChartDroid"; 

	List<EventDatum> event_list = new ArrayList<EventDatum>(); 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.manual_datasets_activity);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.titlebar_icon);


    	final EditText edit_text = (EditText) findViewById(R.id.datum_value_field);
    	final DatePicker date_picker = (DatePicker) findViewById(R.id.date_picker_widget);
        findViewById(R.id.button_add_datum).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

            	Date date = new Date(
            			date_picker.getYear() - 1900,
            			date_picker.getMonth(),
            			date_picker.getDayOfMonth());

            	EventDatum event_datum = new EventDatum();
            	event_datum.label = "Something";
            	event_datum.timestamp = date.getTime();
            	event_datum.value = Float.parseFloat(edit_text.getText().toString());
            	event_list.add(event_datum);
            	
            	((BaseAdapter) getListView().getAdapter()).notifyDataSetChanged();
            }
        });
        

        findViewById(R.id.button_graph_manual_data).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

            	// Procedure: Save the event list to the database,
            	// retrieve the URI for that dataset, and finally
            	// pass along the URI to an intent to launch the chart implicitly.
            	
            	
            	DatabaseStoredData database = new DatabaseStoredData(InputDatasetActivity.this);
            	long dataset_id = database.storeEvents(event_list);
            	// Derive URI from dataset_id.
            	Uri target_uri = LocalStorageContentProvider.constructUri(dataset_id);
            	
                Intent i = new Intent(Intent.ACTION_VIEW, target_uri);
                i.putExtra(Intent.EXTRA_TITLE, "Manual timeline");
                Market.intentLaunchMarketFallback(InputDatasetActivity.this, Market.MARKET_PACKAGE_SEARCH_STRING, i, Market.NO_RESULT);
            }
        });
        
        setListAdapter(new SimpleEventAdapter(this));
    }

    
    public static class EventDatum {
    	public long timestamp;
    	public float value;
    	public String label;
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
        {
			Uri flickr_destination = Uri.parse( Demo.GOOGLE_CODE_URL );
        	// Launches the standard browser.
        	startActivity(new Intent(Intent.ACTION_VIEW, flickr_destination));

            return true;
        }
        case R.id.menu_more_apps:
        {
	    	Uri market_uri = Uri.parse("market://search?q=" + Market.MARKET_AUTHOR_SEARCH_STRING);
	    	Intent i = new Intent(Intent.ACTION_VIEW, market_uri);
	    	startActivity(i);
            return true;
        }
        }

        return super.onOptionsItemSelected(item);
    }


    class SimpleEventAdapter extends BaseAdapter {

        DateFormat date_format;
    	Context context;
        private LayoutInflater mInflater;
    	SimpleEventAdapter(Context context) {
    		this.context = context;
    		this.mInflater = LayoutInflater.from(context);
    		this.date_format = new SimpleDateFormat("MMM d, yyyy");
    	}
    	
		@Override
		public int getCount() {
			return event_list.size();
		}

		@Override
		public Object getItem(int position) {
			return event_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolderEvent {
		    public TextView label, value, date;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolderEvent holder;
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.list_item_event, null);

	            // Creates a ViewHolder and store references to the two children views
	            // we want to bind data to.
	            holder = new ViewHolderEvent();
	            holder.label = (TextView) convertView.findViewById(R.id.timeline_datum_label);
	            holder.value = (TextView) convertView.findViewById(R.id.timeline_datum_value);
	            holder.date = (TextView) convertView.findViewById(R.id.timeline_datum_date);

	            convertView.setTag(holder);

	        } else {

	            // Get the ViewHolder back to get fast access to the TextView
	            // and the ImageView.
	            holder = (ViewHolderEvent) convertView.getTag();
	        }

	        EventDatum event = (EventDatum) event_list.get(position);
	        holder.label.setText(event.label);
	        holder.value.setText( "" + event.value );
	        holder.date.setText( date_format.format(new Date(event.timestamp)) );

	        return convertView;
		}
    }
}