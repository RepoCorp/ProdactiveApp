package co.com.zeitgeist.prodactiveapp.helpers;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.com.zeitgeist.prodactiveapp.config.Preferences;

/**
 * Created by D on 23/08/2014.
 */
public class Utils {
    private static Utils instance;

    private Integer Steps;
    private Integer LastSteps;
    //private String  user;
    //private Object  obj = new Object();
    //private boolean sw  = false;
    private final Preferences     p;

    private final Object mutex = new Object();

    public static Utils GetInstance(SharedPreferences prf)
    {
        if(instance==null)
        {
            instance = new Utils(prf);
        }
        return instance;
    }
    private Utils(SharedPreferences prf)
    {
        p     = Preferences.GetInstance(prf);
        int i=p.GetUltimoReporte();
        if(i==-1)
            Steps = LastSteps = 0;
        else
        {
            Steps= LastSteps = i;
        }
    }

    public Integer getSteps() {
        return Steps;
    }
    public int incrementSteps()
    {
        synchronized (mutex)
        {
            Steps++;
            return Steps;
        }
    }
    public void setSteps(Integer steps) {

        Steps = steps;
    }
    public Integer GetStepsFromLastReport()
    {
        synchronized (mutex)
        {
            Integer pasos;
            if(LastSteps==0)
                pasos=Steps;
            else
                pasos=Steps-LastSteps;

            return pasos;
        }
    }

    public void UpdateLastStep(Integer value)
    {
        LastSteps += value;
        p.SaveUltimoReporte(LastSteps);
    }
    public String GetUser()
    {
        return p.GetUserPass()[0];
    }

    public void SetWeight(int weight)
    {
        p.PutWeigth(weight);
    }

    public void SetHeight(float height)
    {
        p.PutHeight(height);
    }
    public String GetSex()
    {
        return  p.GetSexo();
    }

    public int GetWeight()
    {
        return p.GetWeight();
    }

    public float GetHeight()
    {
        return p.GetHeight();
    }

    public boolean IsSameDay() {

        Date d= GetCurrentDate();
        Date n= new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return(formatter.format(d).equals(formatter.format(n)));

    }

    public Date GetCurrentDate()
    {
        return p.GetCurrentDay();
    }

    public void SetCurrentDate(Date fecha)
    {
        p.SetCurrentDay(fecha);
    }

    public String[] GetUserPass() {
        return p.GetUserPass();
    }

    public void SaveUser(String user,String pass)
    {
        p.SaveUserPass(user,pass);
    }

    public void RestartSteps() {
        LastSteps = 0;
        Steps     = 0;
        p.SaveUltimoReporte(0);
    }
}
