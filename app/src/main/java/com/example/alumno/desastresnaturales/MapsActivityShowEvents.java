package com.example.alumno.desastresnaturales;

import android.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

/**
 * Created by SykesMod on 6/30/2017.
 */
public class MapsActivityShowEvents extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double lat = 0.0D;
    private Double lng = 0.0D;
    private String descripcion;
    private int tipo_evento;
    private View mView;
    private FirebaseDatabase db;
    private DatabaseReference refEvento;
    //private DatabaseReference refRiesgo;
    //private DatabaseReference refAiuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_show_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseDatabase.getInstance();
        //dbRef = db.getReference();
        refEvento = FirebaseDatabase.getInstance().getReference("eventos");

        //inicializa fragmento de mapa
        if (this.mMap == null) {
            ((MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment_show_events)).getMapAsync(this);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //boton de ir a localizacion actual, no se muestra si el usuario no dio permiso de usar localizacion del dispositivo
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        }
        else
            mMap.setMyLocationEnabled(true);

        //controles de zoom e inclinacion de camara con dos dedos
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        //la vista se centra en el centro de Peru
        Object centroPeru = CameraUpdateFactory.newLatLngZoom(new LatLng(-9.190207, -75.015111), 5.5F);
        mMap.animateCamera((CameraUpdate)centroPeru,1000,null);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        refEvento.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    if (child.getValue(Evento.class).tipo == 0) mMap.addMarker(new MarkerOptions().position(new LatLng(child.getValue(Evento.class).lat,child.getValue(Evento.class).lng)).title(child.getValue(Evento.class).desc).icon(BitmapDescriptorFactory.defaultMarker(HUE_RED)).snippet("Registrado por: " + child.getValue(Evento.class).autor + "\nFecha: " + DateFormat.getInstance().format(child.getValue(Evento.class).fecha)));
                    if (child.getValue(Evento.class).tipo == 1) mMap.addMarker(new MarkerOptions().position(new LatLng(child.getValue(Evento.class).lat,child.getValue(Evento.class).lng)).title(child.getValue(Evento.class).desc).icon(BitmapDescriptorFactory.defaultMarker(HUE_YELLOW)).snippet("Registrado por: " + child.getValue(Evento.class).autor + "\nFecha: " + DateFormat.getInstance().format(child.getValue(Evento.class).fecha)));
                    if (child.getValue(Evento.class).tipo == 2) mMap.addMarker(new MarkerOptions().position(new LatLng(child.getValue(Evento.class).lat,child.getValue(Evento.class).lng)).title(child.getValue(Evento.class).desc).icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN)).snippet("Registrado por: " + child.getValue(Evento.class).autor + "\nFecha: " + DateFormat.getInstance().format(child.getValue(Evento.class).fecha)));
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("error: " ,firebaseError.getMessage());
                Toast.makeText(getApplicationContext(),"Error al consultar lista de Desastres...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



