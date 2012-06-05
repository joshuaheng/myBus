package edu.comp.myBus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavoritesOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME="favorites.db";
	static final String TABLE_NAME = "favorites";
	private static final String TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME
			+ " (stop_id TEXT PRIMARY KEY, code TEXT, stop_lat TEXT, stop_lon TEXT, stop_name TEXT);";

	FavoritesOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FavoritesOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
		onCreate(db);
	}
}