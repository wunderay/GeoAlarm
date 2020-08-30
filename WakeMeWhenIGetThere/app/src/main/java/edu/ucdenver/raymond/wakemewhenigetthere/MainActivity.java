package edu.ucdenver.raymond.wakemewhenigetthere;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Alarm> alarmList;
    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private DataManager dataManager;
    LocationManager locationManager;
    LocationListener locationListener;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAlarmDialog addAlarmDialog = new AddAlarmDialog();
                addAlarmDialog.show(getSupportFragmentManager(),"");
            }
        });
        alarmList = new ArrayList<Alarm>();
        dataManager = new DataManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        alarmAdapter = new AlarmAdapter(this, alarmList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager
                .VERTICAL));
        recyclerView.setAdapter(alarmAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void turnOff(){
        locationManager.removeUpdates(locationListener);
    }


    public void startTracking(Alarm alarm){
        final Alarm inUse = alarm;
        inUse.setOn(true);  //if you are calling this, it must be ON

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        else{

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    inUse.calcDistanceTo(latitude,longitude);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        }

    }

    public void addNewAlarm(Alarm alarm){
        String name =alarm.getAlarmName();
        String street = alarm.getStreet();
        String city = alarm.getCity();
        String state = alarm.getMyState();
        String zip = alarm.getZipcode();
        double radius = alarm.getRadius();
        double latitude = alarm.getLatitude();
        double longitude = alarm.getLongitude();
        boolean onOff =  alarm.getOn();
        dataManager.insert(name, street, city, state, zip, radius, latitude, longitude, onOff);
        loadData();
    };

    public void removeAlarm (Alarm alarm) {
        dataManager.delete(alarm.getAlarmId());
        Log.i ("info", Integer.toString(alarmList.size()));
        loadData();
    }

    public void createNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity. this,
                default_notification_channel_id )
                .setSmallIcon(R.drawable. ic_launcher_foreground )
                .setContentTitle( "You have arrived!" )
                .setContentText( "Woken When You Got There" );
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(this. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                    .setUsage(AudioAttributes. USAGE_ALARM )
                    .build() ;
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }

    public void apiFailed(String alarmName){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity. this,
                default_notification_channel_id )
                .setSmallIcon(R.drawable. ic_launcher_foreground )
                .setContentTitle( alarmName )
                .setContentText( "We could not find the location of your alarm. Please delete and try again." );
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(this. NOTIFICATION_SERVICE ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes. CONTENT_TYPE_SONIFICATION )
                    .setUsage(AudioAttributes. USAGE_ALARM )
                    .build() ;
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new
                    NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            notificationChannel.enableLights( true ) ;
            notificationChannel.setLightColor(Color. RED ) ;
            notificationChannel.enableVibration( true ) ;
            notificationChannel.setVibrationPattern( new long []{ 100 , 200 , 300 , 400 , 500 , 400 , 300 , 200 , 400 }) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis (), mBuilder.build()) ;
    }

    public void sendRequestResponse(final Alarm alarm){
        String url ="https://geocoding.geo.census.gov/geocoder/geographies/address?street=" + alarm.getStreet()
                + "&city=" + alarm.getCity()+ "&state=" + alarm.getMyState()+ "&benchmark=Public_AR_Census2010" +
                "&vintage=Census2010_Census2010&layers=14&format=json";
        url = url.replaceAll(" ", "+");
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest requestString = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                alarm.makeLatLong(response);
                addNewAlarm(alarm);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", "API request" + error.toString());
            }
        });

        requestQueue.add(requestString);
    }

    public void showAlarm (int contactToShow) {
        ViewAlarmDialog viewAlarmDialog = new ViewAlarmDialog();
        viewAlarmDialog.sendAlarm(alarmList.get(contactToShow));
        viewAlarmDialog.show(getSupportFragmentManager(), "");
    }

    public void loadData(){
        Cursor cursor = dataManager.selectAll();
        int alarmCount = cursor.getCount();

        if (alarmCount>0){
            alarmList.clear();
            while(cursor.moveToNext()){
                String name = cursor.getString(1);
                String street = cursor.getString(2);
                String city = cursor.getString(3);
                String state = cursor.getString(4);
                String zip = cursor.getString(5);
                double distance = Double.parseDouble(cursor.getString(6));
                double latitude = Double.parseDouble(cursor.getString(7));
                double longitude =Double.parseDouble(cursor.getString(8));
                int id =  Integer.parseInt(cursor.getString(0));
                int convert = Integer.parseInt(cursor.getString(9));
                boolean tf = false;
                if(convert == 1){tf = true;}
                else{
                    tf = false;
                }
                Alarm alarm = new Alarm(name, street, city, state, zip, distance, latitude,
                        longitude, id, tf);
                alarmList.add(alarm);
            }
        }
    alarmAdapter.notifyDataSetChanged();
        Log.i("LoadData", Integer.toString(alarmAdapter.getItemCount()));
    }
}