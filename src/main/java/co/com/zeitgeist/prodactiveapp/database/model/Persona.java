package co.com.zeitgeist.prodactiveapp.database.model;

import android.content.ContentValues;

import java.util.HashMap;

import co.com.zeitgeist.prodactiveapp.database.Insertable;
import co.com.zeitgeist.prodactiveapp.database.TablaPersona;

/**
 * Created by D on 22/08/2014.
 */
public class Persona implements Insertable {


    public Persona()
    {
        Cuentas = new HashMap<String,String>();
    }

    public String  Id ;
    public String  Type;
    public String  Nombre;
    public String  Apellido;
    public Integer Identificacion;
    public String  FechaNacimiento;
    public String  Sexo;
    public Double  Peso;
    public Double  Estatura;

    private HashMap<String,String> Cuentas;

    public String        GetTableName    ()
    {
        return TablaPersona.TABLE_NAME;
    }

    public String        GetWhereClause() {
        return TablaPersona.CN_IDENTIFICACION+"="+Identificacion;
    }

    public ContentValues GetContentValues()
    {
        ContentValues cv= new ContentValues();
        cv.put(TablaPersona.CN_IDENTIFICACION,Identificacion);
        cv.put(TablaPersona.CN_NOMBRE,Nombre);
        cv.put(TablaPersona.CN_APELLIDO,Apellido);
        cv.put(TablaPersona.CN_FECHA_NACIMIENTO,FechaNacimiento);
        cv.put(TablaPersona.CN_SEXO,Sexo);
        cv.put(TablaPersona.CN_PESO,Peso);
        cv.put(TablaPersona.CN_ESTATURA,Estatura);
        return cv;
    }





}