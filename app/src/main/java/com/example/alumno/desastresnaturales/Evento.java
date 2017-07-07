package com.example.alumno.desastresnaturales;

import java.util.Date;

/**
 * Created by SykesMod on 6/30/2017.
 */

public class Evento {
    public String autor;
    public int tipo;
    public String desc;
    public Double lat;
    public Double lng;
    public Date fecha;
    public String id;

    public Evento(){
        tipo=0;
        desc="-null desc-";
        lat=00d;
        lng=00d;
        autor="-null event-";
        fecha=new Date();
        id = "no-id";
    }

    public Evento(int t,String d, Double lt,Double ln,String a,Date f,String i){
        tipo=t;
        desc=d;
        lat=lt;
        lng=ln;
        autor = a;
        fecha = f;
        id = i;
    };


}
