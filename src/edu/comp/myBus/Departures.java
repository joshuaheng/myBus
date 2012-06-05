package edu.comp.myBus;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.comp.myBus.containers.Bus;
import edu.comp.myBus.network.Mtd;

public class Departures extends ListActivity {
	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<Bus> buses = null;
	private String stop_id;
	private BusAdapter m_adapter;
	private Runnable viewBuses;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		
		buses = new ArrayList<Bus>();
		this.m_adapter = new BusAdapter(this, R.layout.bus_list_item, buses);
		setListAdapter(this.m_adapter);

		viewBuses = new Runnable() {
			@Override
			public void run() {
				//retrieve data from mtd server on separate thread
				getBuses();
			}
		};
		
		//worker thread
		Thread thread = new Thread(null, viewBuses);
		thread.start();

		//notify user of current status
		m_ProgressDialog = ProgressDialog.show(Departures.this,
				"Please wait...", "Retrieving data ...", true);

	}

	private Runnable returnRes = new Runnable() {
		//updates listview
		@Override
		public void run() {
			if(buses.size()==0) {
				TextView empty = (TextView) getListView().getEmptyView();
				empty.setText("No results found.");
			}
			if (buses != null && buses.size() > 0) {
				m_adapter.notifyDataSetChanged();
				for (int i = 0; i < buses.size(); i++)
					m_adapter.add(buses.get(i));
			}
			m_ProgressDialog.dismiss();
			m_adapter.notifyDataSetChanged();
		}
	};

	private void getBuses() {
		try {
			stop_id = getIntent().getStringExtra("stop_id");
			//send http request
			Bus[] data = Mtd.getDeparturesByStop(stop_id);
			buses = new ArrayList<Bus>(Arrays.asList(data));

		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private class BusAdapter extends ArrayAdapter<Bus> {

		private ArrayList<Bus> items;

		public BusAdapter(Context context, int textViewResourceId,
				ArrayList<Bus> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.bus_list_item, null);
			}
			Bus o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText(o.headsign);
				}
				if (bt != null) {
					bt.setText("Arriving in: " + o.expected_mins + " mins");
				}
			}
			return v;
		}
	}
}
