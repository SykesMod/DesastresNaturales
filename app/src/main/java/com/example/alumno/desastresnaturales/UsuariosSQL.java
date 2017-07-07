package com.example.alumno.desastresnaturales;

/**
 * Created by alumno on 16/06/2017.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nathaly on 16/04/2017.
 */

public class UsuariosSQL extends SQLiteOpenHelper
{
    String sqlTiposUsuarios = "CREATE TABLE TiposUsuarios (Id INTEGER, Nombre TEXT)";
    String sqlUsuario = "CREATE TABLE Usuario (Contrasenia TEXT, IdTipo INTEGER, Usuario TEXT)";
    String sqlUsuarioEntidadPublica = "CREATE TABLE UsuarioEntidadPublica (Contrasenia TEXT, Dni TEXT, IdTipo INTEGER, Usuario TEXT)";
    String sqlImagenes = "CREATE TABLE Imagenes (Id INTEGER, IdDistrito INTEGER, Imagen BLOB, Usuario TEXT)";
    String sqlDistrito = "CREATE TABLE Distrito (Id INTEGER, IdRegion INTEGER, IdTipo INTEGER, Nombre TEXT)";
    String sqlRegion = "CREATE TABLE Region (Id INTEGER, Nombre TEXT)";
    String sqlTipoDesastre = "CREATE TABLE TipoDesastre (Id INTEGER, Nombre TEXT)";

    public UsuariosSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(sqlTiposUsuarios);
        db.execSQL(sqlUsuario);
        db.execSQL(sqlUsuarioEntidadPublica);
        db.execSQL(sqlImagenes);
        db.execSQL(sqlDistrito);
        db.execSQL(sqlRegion);
        db.execSQL(sqlTipoDesastre);

        insertarTiposUsuarios(db);
        insertarUsuario(db);
        insertarUsuarioEntidadPublica(db);
    }

    public void insertarTiposUsuarios (SQLiteDatabase db)
    {
        String sqlTipoUsuariosComunes = "INSERT INTO TiposUsuarios (Id,Nombre)" + "VALUES (1, 'Usuarios Comunes');";
        String sqlTipoUsuariosEntidad = "INSERT INTO TiposUsuarios (Id,Nombre)" + "VALUES (2, 'Usuarios Entidad Publica');";

        db.execSQL(sqlTipoUsuariosComunes);
        db.execSQL(sqlTipoUsuariosEntidad);
    }

    public void insertarUsuario (SQLiteDatabase db)
    {
        String sqlInsert = "INSERT INTO Usuario (Contrasenia,Dni,IdTipo,Usuario)" + "VALUES ('Usuario123', 1, 'Usuariocomun');";

        db.execSQL(sqlInsert);
    }

    public void insertarUsuarioEntidadPublica (SQLiteDatabase db)
    {
        String sqlInsert = "INSERT INTO UsuarioEntidadPublica (Contrasenia,IdTipo,Usuario)" + "VALUES ('Nat123', '46824316', 2, 'Nathaly');";

        db.execSQL(sqlInsert);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS TiposUsuarios");
        db.execSQL("DROP TABLE IF EXISTS Usuario");
        db.execSQL("DROP TABLE IF EXISTS UsuarioEntidadPublica");
        db.execSQL("DROP TABLE IF EXISTS Imagenes");
        db.execSQL("DROP TABLE IF EXISTS Distrito");
        db.execSQL("DROP TABLE IF EXISTS Region");
        db.execSQL("DROP TABLE IF EXISTS TipoDesastre");

        db.execSQL(sqlTiposUsuarios);
        db.execSQL(sqlUsuario);
        db.execSQL(sqlUsuarioEntidadPublica);
        db.execSQL(sqlImagenes);
        db.execSQL(sqlDistrito);
        db.execSQL(sqlRegion);
        db.execSQL(sqlTipoDesastre);

        insertarTiposUsuarios(db);
        insertarUsuario(db);
        insertarUsuarioEntidadPublica(db);
    }
}
