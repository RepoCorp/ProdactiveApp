package co.com.zeitgeist.prodactiveapp.database.model;

import android.content.ContentValues;

import co.com.zeitgeist.prodactiveapp.database.Insertable;
import co.com.zeitgeist.prodactiveapp.database.TablaLogDiario;

/**
 * Created by D on 19/09/2014.
 */
public class LogDiario implements Insertable {

        public Integer  Id;
        public String  Usuario;
        public String  Fecha;
        public Integer Conteo;
        public Double  Velocidad;

        @Override
        public ContentValues GetContentValues()
        {
            ContentValues cv= new ContentValues();
            cv.put(TablaLogDiario.CN_USUARIO,Usuario);
            cv.put(TablaLogDiario.CN_FECHA,Fecha);
            cv.put(TablaLogDiario.CN_CONTEO,Conteo);
            cv.put(TablaLogDiario.CN_VELOCIDAD,Velocidad);
            return cv;
        }

        @Override
        public String GetTableName()
        {
            return TablaLogDiario.TABLE_NAME;
        }

        @Override
        public String GetWhereClause()
        {
            return TablaLogDiario.CN_ID+"="+Id;
        }
    }

