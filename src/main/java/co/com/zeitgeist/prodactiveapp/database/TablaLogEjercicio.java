package co.com.zeitgeist.prodactiveapp.database;

import android.database.Cursor;

import co.com.zeitgeist.prodactiveapp.database.model.LogEjercicio;

/**
 * Created by D on 22/08/2014.
 */
public class TablaLogEjercicio implements ITable {

    public static final String TABLE_NAME = "LogEjercicio";

    public static final String CN_ID        = "Id";
    public static final String CN_USUARIO   = "Usuario";
    public static final String CN_FECHA     = "Fecha";
    public static final String CN_UBICACION = "Ubicacion";
    public static final String CN_CONTEO    = "Conteo";
    public static final String CN_VELOCIDAD = "Velocidad";

    public static final String CN_REPORTADO = "Reportado";

    private static final String CREATE_TABLE =
            "create table " + TABLE_NAME + " ( "
                    + CN_ID         + " integer primary key autoincrement, "
                    + CN_USUARIO    + " text not null, "
                    + CN_FECHA      + " text not null, "
                    + CN_UBICACION  + " text, "
                    + CN_CONTEO     + " integer, "
                    + CN_VELOCIDAD  + " real, "
                    + CN_REPORTADO  + " integer);";


    @Override
    public String CreateTableSQL() {
        return CREATE_TABLE;
    }

    @Override
    public String SelectAll()
    {

      return  "select " + CN_ID         + " , "
                        + CN_USUARIO    + " , "
                        + CN_FECHA      + " , "
                        + CN_UBICACION  + " , "
                        + CN_CONTEO     + " , "
                        + CN_VELOCIDAD  + "  "
             + " from " + TABLE_NAME    + ";";
    }

    @Override
    public Insertable SerializeItem(Cursor c) {
        LogEjercicio l  = new LogEjercicio();
        l.Id            = c.getInt   (c.getColumnIndex(CN_ID));
        l.Usuario       = c.getString(c.getColumnIndex(CN_USUARIO));
        l.Fecha         = c.getString(c.getColumnIndex(CN_FECHA));
        l.Ubicacion     = c.getString(c.getColumnIndex(CN_UBICACION));
        l.Conteo        = c.getInt   (c.getColumnIndex(CN_CONTEO));
        l.Velocidad     = c.getDouble(c.getColumnIndex(CN_VELOCIDAD));
        return l;
    }
}
