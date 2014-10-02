package co.com.zeitgeist.prodactiveapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import co.com.zeitgeist.prodactiveapp.database.DbHelper;
import co.com.zeitgeist.prodactiveapp.database.Insertable;
import co.com.zeitgeist.prodactiveapp.database.TablaLogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.LogEjercicio;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;
import co.com.zeitgeist.prodactiveapp.helpers.RestServiceAsyncTask;

public class UploadService extends Service {
    private Utils utils;
    private DbHelper Db;
    private String User;

    public UploadService() {



    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    public void load(Intent intent)
    {
        utils = Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        try {

            if (intent.hasExtra("User")) {
                User = intent.getStringExtra("User");
            } else
                User = utils.GetUser();

        } catch (NullPointerException ex){
            Log.e("Error en el intent","null");
        }

        EnvioLogServicio els    = new EnvioLogServicio();
        Timer timer             = new Timer();
        Integer timeOutReport   = 5 * 60 * 1000;
        //Integer timeOutReport   = 40 * 1000;
        timer.schedule(els, timeOutReport, timeOutReport);
        //timer.schedule(els, 10000, timeOutReport);
        Db= DbHelper.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //utils = Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        load(intent);
        return mBinder;
    }

    private final IBinder       mBinder = new UploadBinder();

    public class UploadBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }


    private class EnvioLogServicio extends TimerTask
    {
        @Override
        public void run() {

            Log.i("EnvioLogServicio", "Se ha iniciado reporte");
            TablaLogEjercicio t= new TablaLogEjercicio();
            for (Insertable insertable : Db.Select(t.SelectAll(), t)) {
                LogEjercicio le = (LogEjercicio) insertable;
                if (le.Usuario.equals("")) {
                    if(User==null)
                    {
                        User=utils.GetUser();
                    }
                    le.Usuario = User;
                }
                RestServiceAsyncTask rest= new RestServiceAsyncTask(Db);
                rest.execute(le);
            }
        }
    }
}


