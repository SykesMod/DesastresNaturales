package com.example.alumno.desastresnaturales;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.internal.wp;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by SykesMod on 7/4/2017.
 */

public class EventsManager extends BaseActivity {

    private String userEmail;

    public String username;
    public View mView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = LayoutInflater.from(this).inflate(R.layout.activity_manage_events, null);
        setContentView(R.layout.activity_manage_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView listView = (ListView) findViewById(R.id.manage_events_list);

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
                }
                DatabaseReference test = FirebaseDatabase.getInstance().getReference("eventos");
                final Query testQuery = test.orderByValue();
                FirebaseListAdapter<Evento> fbAdapter = new FirebaseListAdapter<Evento>(EventsManager.this,Evento.class,R.layout.event_adapter,testQuery) {
                    @Override
                    protected void populateView(final View v, final Evento model, int position) {
                        ImageView tipo = (ImageView)v.findViewById(R.id.event_item_type);
                        TextView titulo = (TextView)v.findViewById(R.id.event_item_title);
                        TextView fecha = (TextView)v.findViewById(R.id.event_title_date);
                        ImageView eliminar = (ImageView)v.findViewById(R.id.event_item_delete);

                        Log.d("ListAdapter","evento de tipo "+model.tipo);

                        switch (model.tipo){
                            case 0: tipo.setImageResource(R.drawable.marker_red);break;
                            case 1: tipo.setImageResource(R.drawable.marker_yellow);break;
                            case 2: tipo.setImageResource(R.drawable.marker_green);break;
                        }
                        titulo.setText(model.desc);
                        fecha.setText(model.fecha.toLocaleString());
                        eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog deleteDialog = new AlertDialog.Builder(EventsManager.this)
                                        .setTitle("Eliminar evento")
                                        .setMessage("Desea eliminar este evento?")
                                        .setNegativeButton("Atras",null)
                                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                v.setVisibility(View.GONE);
                                                FirebaseDatabase.getInstance().getReference("eventos").child(model.id).removeValue();
                                            }
                                        }).create();
                                deleteDialog.show();
                            }
                        });
                    }
                };
                listView.setAdapter(fbAdapter);
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
