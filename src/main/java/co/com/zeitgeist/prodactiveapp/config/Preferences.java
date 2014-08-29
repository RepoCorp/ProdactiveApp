package co.com.zeitgeist.prodactiveapp.config;

import android.content.SharedPreferences;

/**
 * Created by D on 22/08/2014.
 */
public class Preferences {
    private SharedPreferences prf;
    private static String User      = "co.com.zeitgeist.prodactiveapp.user";
    private static String Pass      = "co.com.zeitgeist.prodactiveapp.pass";
    private static String Contador  = "co.com.zeitgeist.prodactiveapp.contador";

    private static String Peso      = "co.com.zeitgeist.prodactiveapp.peso";
    private static String Altura    = "co.com.zeitgeist.prodactiveapp.altura";
    private static String Sexo      = "co.com.zeitgeist.prodactiveapp.sexo";


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
        prf.edit().putInt(Contador,contador);
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
}
