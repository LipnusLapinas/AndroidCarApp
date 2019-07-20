package com.hfad.danieliausapp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ArrayList<Integer> id = new ArrayList<Integer>();
    ArrayList<String> plateNumber = new ArrayList<String>();
    ArrayList<Double> latitude = new ArrayList<Double>();
    ArrayList<Double> longitude = new ArrayList<Double>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> photoUrl = new ArrayList<String>();
    ArrayList<Integer> batteryPercentage = new ArrayList<Integer>();

    private GoogleMap mMap;
    String url = "https://development.espark.lt/api/mobile/public/availablecars";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /*
         * fetch json data from url in the form of json string using volley library
         * */

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                parseJsonData(string);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MapsActivity.this);
        rQueue.add(request);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    void parseJsonData(String jsonString) {         //nuskaito visą json iš url
        try {

            JSONArray masinos = new JSONArray(jsonString);
            for (int i = 0; i < masinos.length(); i++) {
                JSONObject masina = masinos.getJSONObject(i);
                id.add(masina.getInt("id"));
                plateNumber.add(masina.getString("plateNumber"));
                JSONObject location = masina.getJSONObject("location");
                latitude.add(location.getDouble("latitude"));
                longitude.add(location.getDouble("longitude"));
                address.add(location.getString("address"));
                JSONObject model = masina.getJSONObject("model");
                photoUrl.add(model.getString("photoUrl"));
                batteryPercentage.add(masina.getInt("batteryPercentage"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng pirma_masina = new LatLng(latitude.get(0), longitude.get(0));
        mMap.addMarker(new MarkerOptions().position(pirma_masina).title(plateNumber.get(0)));

        for(int i = 1; i < latitude.size(); i++) {
            pirma_masina = new LatLng(latitude.get(i), longitude.get(i));
            mMap.addMarker(new MarkerOptions().position(pirma_masina).title(plateNumber.get(i)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pirma_masina));
    }

}