package com.example.alumno.desastresnaturales;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SykesMod on 7/5/2017.
 */

public class UserManager extends BaseActivity {

    private String userEmail;
    private String userType;
    public String username;
    public View mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(this).inflate(R.layout.activity_manage_users, null);
        setContentView(R.layout.activity_manage_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final ListView listView = (ListView) findViewById(R.id.manage_users_list);

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Bundle extras = getIntent().getExtras();
        if(extras!=null){userEmail=extras.getString("userEmail");}

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query q = ref.orderByChild("email").equalTo(userEmail);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("EVENTSMANAGER USERQUERY", "INSIDE LISTENER, DATA CONTENTS: " + dataSnapshot.getValue());
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    username = child.getValue(Usuario.class).username;
                    userType = child.getValue(Usuario.class).tipoUsuario;
                    Log.d("TIPO DE USER","TIPO: " + userType);
                }
                if (userType.equals("admin")) {
                    //DatabaseReference test = FirebaseDatabase.getInstance().getReference("eventos");
                    final Query testQuery = ref.orderByValue();
                    FirebaseListAdapter<Usuario> fbAdapter = new FirebaseListAdapter<Usuario>(UserManager.this,Usuario.class,R.layout.user_adapter,testQuery) {
                        @Override
                        protected void populateView(View v, final Usuario model, int position) {
                            ImageView tipo_user = (ImageView)v.findViewById(R.id.user_item_type);
                            TextView nombre_user = (TextView)v.findViewById(R.id.user_item_username);
                            TextView email_user = (TextView)v.findViewById(R.id.user_item_email);
                            ImageView administrar = (ImageView)v.findViewById(R.id.user_item_manage);

                            switch (model.tipoUsuario){
                                case "admin": tipo_user.setImageResource(android.R.drawable.sym_def_app_icon);
                                    tipo_user.setColorFilter(Color.parseColor("#03A9F4"));
                                    break;
                                case "entidad": tipo_user.setImageResource(android.R.drawable.sym_def_app_icon);
                                    tipo_user.setColorFilter(Color.parseColor("#E040FB"));
                                    break;
                                case "pendiente": tipo_user.setImageResource(android.R.drawable.sym_def_app_icon);
                                    tipo_user.setColorFilter(Color.parseColor("#FFEE58"));
                                    break;
                                case "comun": tipo_user.setImageResource(android.R.drawable.sym_def_app_icon);
                                    tipo_user.setColorFilter(Color.parseColor("#00E676"));
                                    break;
                            }
                            nombre_user.setText(model.username);
                            email_user.setText(model.email);

                            if (model.tipoUsuario.equals("pendiente")) {
                                administrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog deleteDialog = new AlertDialog.Builder(UserManager.this)
                                                .setTitle("Administrar usuario")
                                                .setMessage("Que desea hacer con este usuario?")
                                                .setNegativeButton("Atras", null)
                                                .setNeutralButton("Aprobar Entidad", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Map<String,Object> userUpdate = new Usuario(model.username,model.email,model.tipoUsuario,model.id).toMap();
                                                        Map<String,Object> update = new HashMap<>();
                                                        update.put("/users/"+model.id,userUpdate);

                                                        FirebaseDatabase.getInstance().getReference().updateChildren(update);
                                                    }
                                                })
                                                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).create();
                                        deleteDialog.show();
                                    }
                                });
                            }
                            else{
                                administrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog deleteDialog = new AlertDialog.Builder(UserManager.this)
                                                .setTitle("Administrar usuario")
                                                .setMessage("Que desea hacer con este usuario?")
                                                .setNegativeButton("Atras", null)
                                                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).create();
                                        deleteDialog.show();
                                    }
                                });
                            }
                        }
                    };
                    listView.setAdapter(fbAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

}
