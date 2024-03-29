package co.com.zeitgeist.prodactiveapp.config;

import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by D on 22/08/2014.
 */
public class Preferences {
    private SharedPreferences prf;
    private static final String User      = "co.com.zeitgeist.prodactiveapp.user";
    private static final String Pass      = "co.com.zeitgeist.prodactiveapp.pass";
    private static final String Contador  = "co.com.zeitgeist.prodactiveapp.contador";
    //private static String TotalStepsDay ="co.com.zeitgeist.prodactiveapp.totalSteps";

    private static final String Peso      = "co.com.zeitgeist.prodactiveapp.peso";
    private static final String Altura    = "co.com.zeitgeist.prodactiveapp.altura";
    private static final String Sexo      = "co.com.zeitgeist.prodactiveapp.sexo";

    private static final String Fecha      = "co.com.zeitgeist.prodactiveapp.fecha";


    private static Preferences instance;

    public static Preferences GetInstance(SharedPreferences prf)
    {
        if(instance==null)
        {
            instance= new Preferences(prf);
        }
        return instance;
    }

    private Preferences(SharedPreferences prf)
    {
        this.prf = prf;
    }

    public void SaveUserPass(String user,String pass)
    {
        prf.edit().putString(User,user).apply();
        prf.edit().putString(Pass,pass).apply();
    }

    public String[] GetUserPass()
    {
        return new String[]{prf.getString(User,""),prf.getString(Pass,"") };
    }

    public void SaveUltimoReporte(int contador)
    {
        prf.edit().putInt(Contador,contador).apply();
    }

    public int GetUltimoReporte()
    {
        return prf.getInt(Contador,-1);
    }

    public void PutWeigth(int peso)
    {
        prf.edit().putInt(Peso,peso).apply();
    }
    public void PutHeight(float altura)
    {
        prf.edit().putFloat(Altura,altura).apply();
    }
    public void PutSexo(String sexo)
    {
        prf.edit().putString(Sexo,sexo).apply();
    }

    public String GetSexo(){return prf.getString(Sexo,"M");}
    public int   GetWeight()
    {
        return prf.getInt(Peso,0);
    }
    public float GetHeight()
    {
        return prf.getFloat(Altura,0);
    }

    public Date GetCurrentDay()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String s=prf.getString(Fecha,"");
            if(s.equals(""))
            {
                Date d= new Date();
                SetCurrentDay(d);
                return d;
            }
            return formatter.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(1970,1,1);
        }
    }

    public void SetCurrentDay(Date fecha)
    {   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        prf.edit().putString(Fecha,formatter.format(fecha)).apply();
    }

    /*public Integer GetTotalStepsDay() {
        return prf.getInt(TotalStepsDay,0);
    }

    public void setTotalStepsDay(Integer value) {
        prf.edit().putInt(TotalStepsDay,value).apply();
    }*/
}
