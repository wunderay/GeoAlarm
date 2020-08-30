package edu.ucdenver.raymond.wakemewhenigetthere;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.Math;

import static java.lang.Math.sqrt;

public class Alarm extends Thread {
    private String alarmName;
    private String fullAddress;
    private String street;
    private String city;
    private String state;
    private String zipcode;
    private double distanceTo;
    private double radius;
    private double latitude;    //denver 39 y
    private double longitude;   //denver -104 x
    private boolean isOn;
    private int id;

    MainActivity mainActivity;

    //constructor is for temporarily creating an alarm object. If you intend to create a permanent
    //alarm object, use the other constructor.
    public Alarm(String alarmName, String street, String city, String state,
            String zipcode, double radius){
        this.alarmName = alarmName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.radius = radius;
        //default coordinates close to state capitol
        isOn = false;
        this.id = 0;
    };

    //ID value is given from SQL table.
    public Alarm(String alarmName, String street, String city, String state,
                 String zipcode, double radius, double latitude, double longitude, int id, boolean on ){
        this.alarmName = alarmName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        isOn = on;
        this.id = id;
    };

    public void passActivity(MainActivity mainActivity){this.mainActivity = mainActivity;}

    public void run(){
        //calls main function
        mainActivity.startTracking(this);
    }

    //stops requesting location updates
    public void onStop(){mainActivity.locationManager.removeUpdates(mainActivity.locationListener);}

    public boolean calcDistanceTo(double currentLat, double currentLong){
        double lat = (currentLat - latitude);
        double lng = (currentLong - longitude);

        //convert from latlng to feet
        lat = lat * 364000; //feet per degree
        lng = lng * 288200; //feet per degree
        lat = lat * lat;
        lng = lng * lng;

        double result = sqrt(lat + lng);//resultant distance


        if (result <= radius){
            Log.i("Done", "Reached Destination");
            setOn(false);   //stop checking gps
            mainActivity.turnOff();
            mainActivity.createNotification();
            return true;    //you've reached the range of your destination
        }
        else {return false; }
    }
    public void setOn(boolean value){isOn = value;}
    public boolean getOn(){return isOn;}

    public void makeLatLong(String address){
        //create JSON object to extract data
        try {
            JSONObject jsonObject = new JSONObject(address);
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray addressMatches = result.getJSONArray("addressMatches");
            JSONObject firstResult = addressMatches.getJSONObject(0);
            JSONObject coordinates = firstResult.getJSONObject("coordinates");
            latitude = coordinates.getDouble("y");
            longitude = coordinates.getDouble("x");

            Log.i("CoY ", Double.toString(latitude));
            Log.i("CoX", Double.toString(longitude));
        } catch (JSONException e) {
            Log.i("Error", e.getMessage());
            e.printStackTrace();
            latitude = 39.7392;
            longitude = -104.9903;
            mainActivity.apiFailed(alarmName);
        }
    }

    public int getAlarmId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getAlarmName() {
        return alarmName;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getMyState() {
        return state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public double getRadius() {
        return radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setRadius(int distance) {
        this.radius = distance;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
