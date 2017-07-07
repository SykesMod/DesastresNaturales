package com.example.alumno.desastresnaturales;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by SykesMod on 7/4/2017.
 */

public class EventListAdapter extends ArrayAdapter {
    Context context;
    LayoutInflater inflater;
    Evento[] eventos = null;
    View.OnClickListener[] verMapaListeners;
    View.OnClickListener[] eliminarListeners;

    public EventListAdapter(Context c, Evento[]events, View.OnClickListener[] viewMapListeners, View.OnClickListener[] deleteListeners) {
        super(c,R.layout.event_adapter,events);
        this.context=c;
        this.eventos=events;
        this.verMapaListeners=viewMapListeners;
        this.eliminarListeners=deleteListeners;
        inflater = LayoutInflater.from(c);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.event_adapter, parent, false);  //do
            Log.d("EventListAdapter: ", "Inflated object " + position);
        }

        ImageView tipo = (ImageView)convertView.findViewById(R.id.event_item_type);
        TextView titulo = (TextView)convertView.findViewById(R.id.event_item_title);
        TextView fecha = (TextView)convertView.findViewById(R.id.event_title_date);
        ImageView eliminar = (ImageView)convertView.findViewById(R.id.event_item_delete);

        switch (eventos[position].tipo) {
            case 0:
                tipo.setImageResource(R.drawable.marker_red);
                break;
            case 1:
                tipo.setImageResource(R.drawable.marker_yellow);
                break;
            case 2:
                tipo.setImageResource(R.drawable.marker_green);
                break;
        }

        titulo.setText(eventos[position].desc);
        fecha.setText(eventos[position].fecha.getDay() + "/" + eventos[position].fecha.getMonth() + "/" + eventos[position].fecha.getYear());
        eliminar.setOnClickListener(eliminarListeners[position]);

        return convertView;
    }


}
