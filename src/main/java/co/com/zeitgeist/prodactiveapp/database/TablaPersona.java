package co.com.zeitgeist.prodactiveapp.database;

import android.database.Cursor;

import co.com.zeitgeist.prodactiveapp.database.model.Persona;

/**
 * Created by D on 22/08/2014.
 */
public class TablaPersona implements ITable {

    public static final String TABLE_NAME = "Persona";

    public static final String CN_NOMBRE            = "Nombre";
    public static final String CN_APELLIDO          = "Apellido";
    public static final String CN_IDENTIFICACION    = "Identificacion";
    public static final String CN_FECHA_NACIMIENTO  = "FechaNacimiento";
    public static final String CN_SEXO              = "Sexo";
    public static final String CN_PESO              = "Peso";
    public static final String CN_ESTATURA          = "Estatura";

    private static final String CREATE_TABLE =
            "create table "+ TABLE_NAME+ " ( "
                    + CN_IDENTIFICACION     + " integer primary key , "
                    + CN_NOMBRE             + " text not null, "
                    + CN_APELLIDO           + " text not null, "
                    + CN_FECHA_NACIMIENTO   + " text not null, "
                    + CN_SEXO               + " text not null, "
                    + CN_ESTATURA           + " real not null, "
                    + CN_PESO               + " real not null); ";

    private static final String SELECT_ALL =
            "select " + CN_IDENTIFICACION     + " , "
                      + CN_NOMBRE             + " , "
                      + CN_APELLIDO           + " , "
                      + CN_FECHA_NACIMIENTO   + " , "
                      + CN_SEXO               + " , "
                      + CN_ESTATURA           + " , "
                      + CN_PESO               + " from "+ TABLE_NAME+"; ";

    @Override
    public String CreateTableSQL() {
        return CREATE_TABLE;
    }

    @Override
    public String SelectAll()
    {
        return SELECT_ALL;
    }
    @Override
    public Insertable SerializeItem(Cursor c) {
        Persona p         = new Persona();
        p.Identificacion  = c.getInt(c.getColumnIndex(CN_IDENTIFICACION));
        p.Nombre          = c.getString(c.getColumnIndex(CN_NOMBRE));
        p.Apellido        = c.getString(c.getColumnIndex(CN_APELLIDO));
        p.FechaNacimiento = c.getString(c.getColumnIndex(CN_FECHA_NACIMIENTO));
        p.Sexo            = c.getString(c.getColumnIndex(CN_SEXO));
        p.Estatura        = c.getDouble(c.getColumnIndex(CN_ESTATURA));
        p.Peso            = c.getDouble(c.getColumnIndex(CN_PESO));
        return p;
    }

}

