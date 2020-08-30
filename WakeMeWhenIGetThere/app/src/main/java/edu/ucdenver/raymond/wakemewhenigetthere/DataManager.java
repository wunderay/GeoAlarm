package edu.ucdenver.raymond.wakemewhenigetthere;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

public class DataManager {
    private SQLiteDatabase db;

    public DataManager(Context context){
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
        db = helper.getWritableDatabase();

    }



    public Cursor selectAll() {

        Cursor cursor = null;
        String query = "select * from alarms order by name";

        try {

            cursor = db.rawQuery(query, null);
            //Log.i ("info", "In Datamanager selectAll try statement");
        } catch (Exception e) {
            Log.i("info", "In DataManager selectAll method");
            Log.i("info", e.getMessage());
        }

        Log.i("info", "Loaded data " + cursor.getCount());
        return cursor;
    }

    public void insert(String name, String street, String city, String state, String zip,
                       double distance, double latitude, double longitude, boolean bool) {
        int conversion = bool ? 1:0;

        String query = "insert into alarms" +
                "(name, street_address, city, state, zip, distance, latitude, longitude, onus) values " +
                "( '" + name + "', '" + street + "','" + city + "', '" + state + "', '" + zip + "" +
                "', '" + Double.toString(distance) + "', '" + Double.toString(latitude) + "', '"
                + Double.toString(longitude) + "', '" + Integer.toString(conversion) + "' )";

        try {

            db.execSQL(query);
        } catch (SQLException e) {
            Log.i("Error", "In DataManager insert method");
            Log.i("Error", e.getMessage());
        }
        Log.i("info", "Added new alarm " + name);
    }
    public void delete(int id) {
        String query = "delete from alarms where _id = " + id + ";";
        try {

            db.execSQL(query);
        } catch (SQLException e) {
            Log.i("info", "In DataManager delete method");
            Log.i("info", e.getMessage());
        }
        Log.i("info", "Deleted alarm ");

    }

    private class MySQLiteOpenHelper extends SQLiteOpenHelper{
        public  MySQLiteOpenHelper(Context context){
            super(context, "alarmsDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String newTable = "create table alarms ("
                    + "_id integer primary key autoincrement not null, "
                    + "name text not null, "
                    + "street_address text, "
                    + "city text, "
                    + "state text, "
                    + "zip text, "
                    + "distance real, "
                    + "latitude real, "
                    + "longitude real, "
                    + "onus integer)";

            try {
                db.execSQL(newTable);
            }
            catch (SQLException e) {
                Log.i ("Error", "In MySQLiteOpenHelper class onCreate method");
                Log.i ("Error", e.getMessage());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //empty
        }
    }
}
