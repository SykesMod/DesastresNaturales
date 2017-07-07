package com.example.alumno.desastresnaturales;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class SubmitEventActivity extends BaseActivity implements OnMapReadyCallback {

    private String userEmail;

    private GoogleMap mMap;
    private int tipo;
    private EditText desc;
    private Double lat = 0.0D;
    private Double lng = 0.0D;
    private FirebaseDatabase db;
    private DatabaseReference dbRef;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            userEmail = extras.getString("userEmail");
        }

        db = FirebaseDatabase.getInstance();

        final Spinner mSpinner = (Spinner) findViewById(R.id.eventos_desplegable);

        //inicializa fragmento de mapa
        if (this.mMap == null) {
            ((MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
        }

        //listener de confirmacion de envio a la bd
        final DatabaseReference.CompletionListener completionListener = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError==null){
                    Toast.makeText(SubmitEventActivity.this,"Enviado!",Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    finish();
                }
                else {
                    Toast.makeText(SubmitEventActivity.this, "Ocurrio un error al enviar, intente nuevamente...", Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                }

            }
        };


        ///////////////////////////////////////////listener de boton enviar
        Button mButton = (Button) findViewById(R.id.enviarEvento_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener nListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface d,int n){
                        tipo = 0 ;
                        String dbChild = "";
                        desc = (EditText) findViewById(R.id.eventos_descripcion);

                        String opcion = ((String) mSpinner.getSelectedItem());
                        switch (opcion){
                            case "Zona de Desastre":
                                tipo = 0;
                                dbChild = "desastre";
                                break;
                            case "Zona de Riesgo":
                                tipo = 1;
                                dbChild = "riesgo";
                                break;
                            case "Entrega de Ayuda":
                                tipo = 2;
                                dbChild = "aiuda";
                                break;
                        }

                        if (desc.getText().toString().isEmpty() || (lat==0 && lng==0)){

                            AlertDialog.Builder dialogoErrorValidacion = new AlertDialog.Builder(SubmitEventActivity.this);
                            dialogoErrorValidacion.setTitle("Error");
                            dialogoErrorValidacion.setMessage("Falta ingresar algun dato, por favor revise e intente nuevamente...");
                            dialogoErrorValidacion.setPositiveButton("Aceptar",null);
                            AlertDialog a = dialogoErrorValidacion.create();
                            a.show();
                        }
                        else {

                            dbRef = db.getReference("eventos");
                            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                            Query qUser = userRef.orderByChild("email").equalTo(userEmail);
                            qUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()){
                                        username = child.getValue(Usuario.class).username;
                                    }

                                    Log.d("EVENT SUBMIT", "USERNAME: " + username);
                                    final String id = FireBasePushIdGenerator.generatePushId();
                                    dbRef.child(id).setValue(new Evento(tipo, desc.getText().toString(), lat, lng, username, new Date(),id), completionListener);
                                    dbRef.push();
                                    showProgressDialog("Enviando...");

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                };

                AlertDialog.Builder dialogoConfirmar = new AlertDialog.Builder(SubmitEventActivity.this);
                dialogoConfirmar.setTitle("Confirmar envio");
                dialogoConfirmar.setMessage("Se creara una nueva entrada en la base de datos con la informacion ingresada, desea continuar?");
                dialogoConfirmar.setIcon(R.mipmap.ic_launcher);
                dialogoConfirmar.setPositiveButton("Aceptar",nListener);
                dialogoConfirmar.setNegativeButton("Cancelar",null);
                //askPerm.setOnDismissListener(null);
                dialogoConfirmar.setCancelable(false);

                AlertDialog a = dialogoConfirmar.create();
                a.show();
            }
        });


    }

    //boton atras
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //boton de ir a localizacion actual, no se muestra si el usuario no dio permiso de usar localizacion del dispositivo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        }
        else
            mMap.setMyLocationEnabled(true);

        //controles de zoom e inclinacion de camara con dos dedos
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        //la vista se centra en el centro de Peru
        Object centroPeru = CameraUpdateFactory.newLatLngZoom(new LatLng(-9.190207, -75.015111), 4.8F);
        mMap.animateCamera((CameraUpdate)centroPeru,1000,null);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            public void onMapClick(LatLng paramAnonymousLatLng)
            {
                mMap.clear();
                MarkerOptions localMarkerOptions = new MarkerOptions().position(new LatLng(paramAnonymousLatLng.latitude, paramAnonymousLatLng.longitude)).title("Marcador");
                mMap.addMarker(localMarkerOptions);
                lat = paramAnonymousLatLng.latitude;
                lng = paramAnonymousLatLng.longitude;
            }
        });


        //LLAMAS A LA BD, RECOLECTAS LOS DATOS, DENTRO DEL FOR mMap.addMarker PARA CADA UNO
        //FirebaseDatabase.getInstance();

    }
}
