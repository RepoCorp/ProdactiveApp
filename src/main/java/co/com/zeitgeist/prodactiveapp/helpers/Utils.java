package co.com.zeitgeist.prodactiveapp.helpers;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import co.com.zeitgeist.prodactiveapp.activity.PedometroActivity;
import co.com.zeitgeist.prodactiveapp.config.Preferences;
import co.com.zeitgeist.prodactiveapp.database.model.ServiceResponse;
import co.com.zeitgeist.prodactiveapp.service.RestService;

/**
 * Created by D on 23/08/2014.
 */
public class Utils {
    private static Utils instance;

    private Integer Steps;
    private Integer LastSteps;
    private String  user;
    private Object  obj = new Object();
    private boolean sw  = false;
    Preferences     p;

    Object mutex = new Object();

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
            Integer pasos=0;
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

        if(new Date() .getYear() == p.GetCurrentDay().getYear() &&
                new Date().getMonth() == p.GetCurrentDay().getMonth() &&
                new Date().getDay() == p.GetCurrentDay().getDay())
        {
            return true;
        }
        else
        {
            //que proceso se hace cuando cabia el dia?
        return false;
        }

    }

    public void SetCurrentDate(Date fecha)
    {
        p.SetCurrentDay(fecha);
    }

    public String[] GetUserPass() {
        return p.GetUserPass();
    }
}
