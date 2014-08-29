package co.com.zeitgeist.prodactiveapp.database.model;

import android.content.ContentValues;

import co.com.zeitgeist.prodactiveapp.database.Insertable;
import co.com.zeitgeist.prodactiveapp.database.TablaLogEjercicio;

/**
 * Created by D on 22/08/2014.
 */
public class LogEjercicio implements Insertable {
    public Integer  Id;
    public String  Usuario;
    public String  Fecha;
    public String  Ubicacion;
    public Integer Conteo;
    public Double  Velocidad;

    @Override
    public ContentValues GetContentValues()
    {
        ContentValues cv= new ContentValues();
        cv.put(TablaLogEjercicio.CN_USUARIO,Usuario);
        cv.put(TablaLogEjercicio.CN_FECHA,Fecha);
        cv.put(TablaLogEjercicio.CN_UBICACION,Ubicacion);
        cv.put(TablaLogEjercicio.CN_CONTEO,Conteo);
        cv.put(TablaLogEjercicio.CN_VELOCIDAD,Velocidad);
        return cv;
    }

    @Override
    public String GetTableName()
    {
        return TablaLogEjercicio.TABLE_NAME;
    }

    @Override
    public String GetWhereClause()
    {
        return TablaLogEjercicio.CN_ID+"="+Id;
    }
}
