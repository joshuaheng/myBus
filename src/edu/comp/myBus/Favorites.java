package edu.comp.myBus;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
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
import edu.comp.myBus.containers.Stop;
import edu.comp.myBus.database.FavoritesDataSource;

public class Favorites extends ListActivity {
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Stop> stops = null;
	private StopAdapter m_adapter;
	private Runnable viewStops;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		stops = new ArrayList<Stop>();
		this.m_adapter = new StopAdapter(this, R.layout.search_list_item, stops);
		setListAdapter(this.m_adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//bus stop chosen. Transition to activity with bus times
				Intent intent = new Intent(Favorites.this, Departures.class);
				intent.putExtra("stop_id", stops.get(position).stop_id);
				startActivity(intent);
			}
		});
		registerForContextMenu(lv);

		viewStops = new Runnable() {
			@Override
			public void run() {
				//retrieve favorites from sqllite database
				getStops();
			}
		};
		
		//worker thread to retrieve data from sqllite
		Thread thread = new Thread(null, viewStops);
		thread.start();

		//notify user of current status
		m_ProgressDialog = ProgressDialog.show(Favorites.this,
				"Please wait...", "Retrieving data ...", true);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.delfav, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.delfav:
			//delete stop from sqllite database
			FavoritesDataSource db = new FavoritesDataSource(this);
			db.open();
			db.deleteStop(stops.get(info.position).stop_id);
			db.close();
			m_adapter.remove(stops.get(info.position));
			//update list view
			m_adapter.notifyDataSetChanged();
			if (m_adapter.isEmpty()) {
				TextView empty = (TextView) getListView().getEmptyView();
				empty.setText("No Favorites.");
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private Runnable returnRes = new Runnable() {

		//update listview
		@Override
		public void run() {
			if (stops.size() == 0) {
				TextView empty = (TextView) getListView().getEmptyView();
				empty.setText("No favorites.");
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
		
		//get favorite stops from database
		try {
			FavoritesDataSource db = new FavoritesDataSource(this);
			db.open();
			stops = db.getAllStops();
			db.close();
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
