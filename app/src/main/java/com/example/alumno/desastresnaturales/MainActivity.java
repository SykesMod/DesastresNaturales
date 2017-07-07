package com.example.alumno.desastresnaturales;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends BaseActivity
{
    private static final int PERMISO_GEOLOCALIZACION = 2084;


    //autenticacion
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase db;
    private DatabaseReference dbRef;

    EditText username;
    EditText password;

    String userEmail;

    EditText registro_user;
    EditText registro_email;
    EditText registro_password;

    Button ingresar;
    Button registrarse;
    Button registro_back;
    Button registro_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        username = (EditText)findViewById(R.id.editTextUsuario);
        password = (EditText)findViewById(R.id.editTextContra);

        registro_user = (EditText)findViewById(R.id.registro_username);
        registro_email = (EditText)findViewById(R.id.registro_email);
        registro_password = (EditText)findViewById(R.id.registro_password);

        /////////Pide permiso para usar localizacion
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2048);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        /////////////////

        ingresar = (Button) findViewById(R.id.button_ingresar);
        ingresar.setClickable(true);
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//////////////////////////////////////////////////////////////////////////////
                iniciarSesion(username.getText().toString().trim(),password.getText().toString().trim());
            }
        });

        registrarse = (Button) findViewById(R.id.button_registrarse);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.main_inicio_sesion).setVisibility(View.GONE);

                findViewById(R.id.main_registro).setVisibility(View.VISIBLE);
                findViewById(R.id.registro_buttons).setVisibility(View.VISIBLE);
                username.setText("");
                password.setText("");
            }
        });

        registro_back = (Button) findViewById(R.id.registro_back);
        registro_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.main_inicio_sesion).setVisibility(View.VISIBLE);

                findViewById(R.id.main_registro).setVisibility(View.GONE);
                findViewById(R.id.registro_buttons).setVisibility(View.GONE);
                registro_user.setText("");
                registro_email.setText("");
                registro_password.setText("");
            }
        });

        registro_submit = (Button) findViewById(R.id.registro_submit);
        registro_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox entidad = (CheckBox) findViewById(R.id.registro_entidad);
                if (entidad.isChecked()) crearCuenta(registro_user.getText().toString().trim(),registro_email.getText().toString().trim(),registro_password.getText().toString().trim(),"pendiente");
                else crearCuenta(registro_user.getText().toString().trim(),registro_email.getText().toString().trim(),registro_password.getText().toString().trim(),"comun");

                //Log.d("CREAR CUENTA", registro_email.getText().toString().trim() + " " + registro_password.getText().toString().trim());
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    // User is signed in
                    Log.d("SESION", "Usuario Autenticado y verificado, iniciando...");
                    Intent i = new Intent(MainActivity.this, MainActivityLoggedIn.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("userEmail",user.getEmail());
                    startActivity(i);
                    finish();
                } else {
                    // User is signed out
                    Log.d("SESION", "No hay Usuario Autenticado");
                }
                // ...
            }
        };


    }

    private void crearCuenta(final String username, final String email, final String password, final String type){
        if (!validarDatosRegistro()) {
            //Log.d("CREAR CUENTA", "DATOS INVALIDOS");
            return;
        }

        showProgressDialog("Registrando...");
        //Log.d("CREAR CUENTA", "CREANDO...");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dbRef = db.getReference("users");
                        Query q = dbRef.orderByChild("username").equalTo(username);

                        if (task.isSuccessful()) {//registro exitoso
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        EditText user = (EditText) findViewById(R.id.registro_username);
                                        user.setError("Nombre de usuario ya existe");
                                    }
                                    else{
                                        final String id = FireBasePushIdGenerator.generatePushId();
                                        dbRef.child(id).setValue(new Usuario(username,email,type,id));
                                        Toast.makeText(MainActivity.this, "Registrado con exito!", Toast.LENGTH_SHORT).show();
                                        enviarVerificacionEmail(mAuth.getCurrentUser());
                                        mAuth.signOut();

                                        findViewById(R.id.main_inicio_sesion).setVisibility(View.VISIBLE);

                                        findViewById(R.id.main_registro).setVisibility(View.GONE);
                                        findViewById(R.id.registro_buttons).setVisibility(View.GONE);

                                    }

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(MainActivity.this, "Error, no se pudo verificar si el nombre de usuario existe", Toast.LENGTH_SHORT).show();
                                    Log.d("CREAR CUENTA","ERROR QUERY USERNAMES " + databaseError);
                                }
                            });
                        } else {//error al registrar usuario
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        EditText user = (EditText) findViewById(R.id.registro_username);
                                        user.setError("Nombre de usuario ya existe");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(MainActivity.this, "Error, no se pudo verificar si el nombre de usuario existe##############", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(MainActivity.this, "Se produjo un error, intente nuevamente.", Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]

    }

    private void iniciarSesion(final String user,final String password) {
        if (!validarDatos()) {
            return;
        }

        showProgressDialog("Iniciando Sesion...");
        //Log.d("INICIAR SESION", "INICIANDO...");

        DatabaseReference ref = db.getReference("users");
        Query q = ref.orderByChild("username").equalTo(user);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("INICIO SESION" , "SNAPSHOT EMAIL: "+ dataSnapshot.getValue());

                if(dataSnapshot.getValue()==null){// si no existe el usuario
                    Toast.makeText(MainActivity.this,"Error, Revise los datos e intente nuevamente",Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    return;
                }

                ////////////////////////////////////////////////////////accede al email de child del snapshot
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    //Log.d("INICIO SESION" , "USUARIO TMP: "+ child.getValue(Usuario.class).email);
                    userEmail = child.getValue(Usuario.class).email;
                }

                if(userEmail.equals("no_email")){
                    AlertDialog.Builder dialogoInfo = new AlertDialog.Builder(MainActivity.this);
                    dialogoInfo.setTitle("Usuario no verificado");
                    dialogoInfo.setMessage("Por favor haga click en el link de verificacion en el correo que se le envio al momento de registrarse para poder iniciar sesion.");
                    dialogoInfo.setPositiveButton("OK",null);
                    dialogoInfo.setCancelable(false);
                    AlertDialog a = dialogoInfo.create();
                    a.show();
                    hideProgressDialog();
                    //return; ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
                else {
                    mAuth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //inicio de sesion
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Inicio de sesion exitoso...", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainActivity.this, MainActivityLoggedIn.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                //error de inicio de sesion
                                Toast.makeText(MainActivity.this, "Error de inicio de sesion, revise los datos e intente nuevamente.", Toast.LENGTH_LONG).show();

                                //updateUI(null);
                            }
                            hideProgressDialog();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("INICIO SESION" , "############CANCELLED!, EMAIL: "+ userEmail);
            }
        });



    }

    private void enviarVerificacionEmail(FirebaseUser u) {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = u;
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Email de verificacion enviado a " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            Log.d("VERIFICACION EMAIL", "EMAIL VERIFICACION ENVIADO");
                        } else {
                            Toast.makeText(MainActivity.this, "Error al enviar email de verificacion.", Toast.LENGTH_SHORT).show();
                            Log.d("VERIFICACION EMAIL", "ERROR ENVIO EMAIL VERIFICACION");
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean validarDatos() {
        boolean valid = true;

        String user = username.getText().toString();
        if (TextUtils.isEmpty(user)) {
            username.setError("Requerido");
            valid = false;
        } else {
            username.setError(null);
        }

        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(passwordText)) {
            password.setError("Requerido");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }
    private boolean validarDatosRegistro() {
        boolean valid = true;

        EditText user = (EditText) findViewById(R.id.registro_username);
        String userText = user.getText().toString();
        if (TextUtils.isEmpty(userText)) {
            user.setError("Requerido");
            valid = false;
        } else {
            user.setError(null);
        }

        EditText user_email = (EditText) findViewById(R.id.registro_email);
        String emailText = user.getText().toString();
        if (TextUtils.isEmpty(emailText)) {
            user_email.setError("Requerido");
            valid = false;
        } else {
            user_email.setError(null);
        }

        EditText pw = (EditText) findViewById(R.id.registro_password);
        String passwordText = pw.getText().toString();
        if (TextUtils.isEmpty(passwordText)) {
            pw.setError("Requerido");
            valid = false;
        } else {
            pw.setError(null);
        }

        return valid;
    }

}
