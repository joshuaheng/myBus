package edu.comp.myBus;

import java.util.ArrayList;
import java.util.Arrays;

import edu.comp.myBus.containers.Stop;
import edu.comp.myBus.database.FavoritesDataSource;
import edu.comp.myBus.network.Mtd;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResults extends ListActivity {
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Stop> stops = null;
	private StopAdapter m_adapter;
	private Runnable viewStops;
	private String query;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			doMySearch(query);
		}
	}

	private void doMySearch(String query) {
		stops = new ArrayList<Stop>();
		this.m_adapter = new StopAdapter(this, R.layout.search_list_item, stops);
		setListAdapter(this.m_adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
				//transition to departures page
				Intent intent = new Intent(SearchResults.this, Departures.class);
				intent.putExtra("stop_id", stops.get(position).stop_id);
				startActivity(intent);
			}
		});
		registerForContextMenu(lv);

		viewStops = new Runnable() {
			@Override
			public void run() {
				getStops();
			}
		};
		
		//make web request on separate thread
		Thread thread = new Thread(null, viewStops);
		thread.start();

		m_ProgressDialog = ProgressDialog.show(SearchResults.this,
				"Please wait...", "Retrieving data ...", true);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.addfav, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.addfav:
			FavoritesDataSource db = new FavoritesDataSource(this);
			db.open();
			long insertId = db.createStop(stops.get(info.position));
			db.close();
			if(insertId==-1){
				//stop not added. already in database. notify user with a toast
				Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private Runnable returnRes = new Runnable() {

		
		//update list view with results
		@Override
		public void run() {
			if (stops.size() == 0) {
				TextView empty = (TextView) getListView().getEmptyView();
				empty.setText("No results found.");
			}
			if (stops != null && stops.size() > 0) {
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < stops.size(); i++)
					m_adapter.add(stops.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private void getStops() {
		try {
			//send web request
			Stop[] data = Mtd.getStopsBySearch(query);
			stops = new ArrayList<Stop>(Arrays.asList(data));

		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private class StopAdapter extends ArrayAdapter<Stop> {

		private ArrayList<Stop> items;

		public StopAdapter(Context context, int textViewResourceId,
				ArrayList<Stop> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.search_list_item, null);
			}
			Stop o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				if (tt != null) {
					tt.setText(o.stop_name);
				}
			}
			return v;
		}
	}
}
