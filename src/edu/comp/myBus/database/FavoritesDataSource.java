package edu.comp.myBus.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import edu.comp.myBus.containers.Stop;

public class FavoritesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private FavoritesOpenHelper dbHelper;
	private String[] allColumns = {"stop_id","code","stop_lat","stop_lon","stop_name"};

	public FavoritesDataSource(Context context) {
		dbHelper = new FavoritesOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createStop(Stop stop) {
		ContentValues values = new ContentValues();
		values.put("stop_id",stop.stop_id);
		values.put("code",stop.code);
		values.put("stop_lat",stop.stop_lat);
		values.put("stop_lon",stop.stop_lon);
		values.put("stop_name",stop.stop_name);
		long insertId = database.insert(FavoritesOpenHelper.TABLE_NAME, null,
				values);
		return insertId;
	}

	public void deleteStop(String stop_id) {
		database.delete(FavoritesOpenHelper.TABLE_NAME, "stop_id = '"+stop_id+"'", null);
	}

	public ArrayList<Stop> getAllStops() {
		ArrayList<Stop> stops = new ArrayList<Stop>();
		Cursor cursor = database.query(FavoritesOpenHelper.TABLE_NAME,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Stop s = cursorToStop(cursor);
			stops.add(s);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return stops;
	}

	private Stop cursorToStop(Cursor cursor) {
		Stop result = new Stop();
		result.stop_id=cursor.getString(0);
		result.code=cursor.getString(1);
		result.stop_lat=cursor.getString(2);
		result.stop_lon=cursor.getString(3);
		result.stop_name=cursor.getString(4);
		return result;

	}

}
