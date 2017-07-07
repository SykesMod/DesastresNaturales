package com.example.alumno.desastresnaturales;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivityLoggedIn extends BaseActivity {

    public String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logged_in);

        userEmail = "test@test.com";
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userEmail = extras.getString("userEmail");
        }



        final LinearLayout nuevo_evento = (LinearLayout) findViewById(R.id.crear_eventos);
        final LinearLayout ver_eventos = (LinearLayout) findViewById(R.id.ver_eventos);
        final LinearLayout adm_eventos = (LinearLayout) findViewById(R.id.administrar_eventos);
        final LinearLayout adm_users = (LinearLayout) findViewById(R.id.administrar_usuarios);
        nuevo_evento.setVisibility(View.GONE);
        adm_eventos.setVisibility(View.GONE);
        adm_users.setVisibility(View.GONE);

        nuevo_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapa = new Intent(getApplicationContext(),SubmitEventActivity.class);
                mapa.putExtra("userEmail",userEmail);
                startActivity(mapa);

            }
        });

        ver_eventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapa = new Intent(getApplicationContext(),MapsActivityShowEvents.class);
                mapa.putExtra("userEmail",userEmail);
                startActivity(mapa);
            }
        });

        adm_eventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventsIntent = new Intent(getApplicationContext(),EventsManager.class);
                eventsIntent.putExtra("userEmail",userEmail);
                startActivity(eventsIntent);
            }
        });

        adm_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventsIntent = new Intent(getApplicationContext(),UserManager.class);
                eventsIntent.putExtra("userEmail",userEmail);
                startActivity(eventsIntent);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query q = ref.orderByChild("email").equalTo(userEmail);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if (child.getValue(Usuario.class).tipoUsuario.equals("comun") || child.getValue().equals("pendiente")) { //caso de usuario normal
                        nuevo_evento.setVisibility(View.GONE);
                    }
                    if (child.getValue(Usuario.class).tipoUsuario.equals("entidad")) { //caso de usuario entidad
                        nuevo_evento.setVisibility(View.VISIBLE);
                        adm_eventos.setVisibility(View.VISIBLE);
                    }
                    if (child.getValue(Usuario.class).tipoUsuario.equals("admin")) { //caso de usuario administrador
                        nuevo_evento.setVisibility(View.VISIBLE);
                        adm_eventos.setVisibility(View.VISIBLE);
                        adm_users.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu_opciones,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //boton atras
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info_app:
                AlertDialog.Builder dialogoInfo = new AlertDialog.Builder(this);
                dialogoInfo.setTitle("Info App");
                dialogoInfo.setMessage("App desarrollada por:\n" + "<desarrollador>");
                dialogoInfo.setIcon(R.mipmap.ic_launcher);
                dialogoInfo.setPositiveButton("OK",null);
                dialogoInfo.setCancelable(false);
                AlertDialog a = dialogoInfo.create();
                a.show();
                return true;
            case R.id.menu_cerrar_sesion:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivityLoggedIn.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}