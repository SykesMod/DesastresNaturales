package com.example.alumno.desastresnaturales;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SykesMod on 7/3/2017.
 */

public class Usuario {
    public String username;
    public String email;
    public String tipoUsuario;
    public String id;

    Usuario(){
        username="test";
        email="no_email";
        tipoUsuario="comun";
        id="-no-id-";
    }

    Usuario(String u,String e,String t,String i){
        username=u;
        email=e;
        tipoUsuario=t;
        id=i;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("tipoUsuario", tipoUsuario);
        result.put("id", id);
        return result;
    }

}
