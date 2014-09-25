package co.com.zeitgeist.prodactiveapp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.res.ObbInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.com.zeitgeist.prodactiveapp.activity.PedometroActivity;
import co.com.zeitgeist.prodactiveapp.database.DbHelper;
import co.com.zeitgeist.prodactiveapp.database.model.LogDiario;
import co.com.zeitgeist.prodactiveapp.database.model.LogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.ServiceResponse;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;

/**
 * Created by D on 22/08/2014.
 */
public class StepService extends Service implements SensorEventListener{

    public final static String Steps        = "Steps";
    public final static String UpdatedSteps = "UpdatedSteps";
    public final static String Paso         = "co.com.zeitgeist.prodactive.PASO";
    private final static String TAG         = "StepDetector";
    public String User="";

    //private static StepService s;

    private SensorManager sensorManager;
    private Sensor        sensor;

    //private final IBinder binderService = new LocalBinder();

    private boolean sw2     = false;
    private final Object mutex    = new Object();

    private final float   mLimit        = 10;
    private final float   mLastValues[] = new float[3*2];
    private final float   mScale[]      = new float[2];
    private float   mYOffset;

    private final float   mLastDirections[] = new float[3*2];
    private final float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private final float   mLastDiff[]       = new float[3*2];
    private int           mLastMatch        = -1;

    private static Utils util;
    private DbHelper Db;

    private Date     lastReport;

    public StepService()
    {

        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));

    }



    private BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(PedometroActivity.MessageToStepService))
            {
                int value=intent.getIntExtra(UpdatedSteps,util.GetStepsFromLastReport());
                util.UpdateLastStep(value);
            }
            else if(intent.getAction().equals(PedometroActivity.InitProdactive))
            {
                SendBroadcast();
            }

        }
    };



    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
/*        Context ctx = getApplicationContext();
        util=Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(ctx));*/
        util=Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
        Log.i("User?",util.GetUserPass()[0]+" " +util.GetUserPass()[1]);

        //receiver   = new ComunicationStepServiceReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(PedometroActivity.RestartCounterOnStepService);
        filter.addAction(PedometroActivity.MessageToStepService);
        filter.addAction(PedometroActivity.InitProdactive);
        registerReceiver(receiver, filter);
        //s=this;
    }

    public void onDestroy()
    {
        util.UpdateLastStep(util.GetStepsFromLastReport());
        unregisterReceiver(receiver);
        SaveLogEjercicio();
        super.onDestroy();
    }

    /*
    public class LocalBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }*/

    //BINDING
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        User = util.GetUser();
        Db= DbHelper.getInstance(this);

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor s=sensorEvent.sensor;
        switch (s.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
            {
                float vSum = 0;
                for (int i=0 ; i<3 ; i++) {
                    final float v = mYOffset + sensorEvent.values[i] * mScale[1];
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;

                float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                if (direction == - mLastDirections[k]) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                    if (diff > mLimit) {

                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                        boolean isPreviousLargeEnough     = mLastDiff[k] > (diff/3);
                        boolean isNotContra               = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            Log.i(TAG, "step");
                            util.incrementSteps();
                            SendBroadcast();
                            Process();
                            //aqui doy aviso del paso.
                            mLastMatch = extType;
                        }
                        else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;

            }
                break;

            default: break;
        }

   }

    private void Process()
    {
        if(lastReport==null)
            lastReport = new Date(SystemClock.elapsedRealtime());
        //verifico si debo reportar
        Date d=new Date(SystemClock.elapsedRealtime());
        if((d.getTime()-lastReport.getTime())>(5*60*1000))
        //if((d.getTime()-lastReport.getTime())>(30000))
        {
            if(util.IsSameDay())
            {
                SaveLogEjercicio();
            }
            else
            {
                SaveLogEjercicio();
                SaveLogDiario();
                util.SetCurrentDate(new Date());
            }
            lastReport = new Date(SystemClock.elapsedRealtime());
        }
    }


    //guarda los valores del contador en la base de datos local.
    private void SaveLogEjercicio() {
        if(!sw2)
        {
            synchronized (mutex)
            {
                if(!sw2)
                {
                    sw2=true;
                    Date    fecha   = new Date();
                    Integer cont    = util.GetStepsFromLastReport();

                    if(cont>0){
                        LogEjercicio le     = new LogEjercicio();
                        le.Velocidad        = (double) 0;
                        le.Conteo           = cont;
                        le.Ubicacion        = "lat=lon=";
                        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HHmmss");
                        le.Fecha            = sdf.format(fecha);
                        le.Usuario          = util.GetUser();

                        Db.Insert(le);
                        //actualizo el valor en el servicio
                        util.UpdateLastStep(cont);
                        Log.i("SaveLogEjercicio" ,"Se ha guardado un registro");
                        Log.i("SaveLogEjercicio User",le.Usuario);
                    }
                    sw2=false;
                }
            }
        }
    }

    private void SaveLogDiario() {
        Date    fecha   = util.GetCurrentDate();
        Integer cont    = util.getSteps();
        if(cont>0){
            LogDiario le     = new LogDiario();
            le.Velocidad        = (double) 0;
            le.Conteo           = cont;
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            le.Fecha            = sdf.format(fecha);
            le.Usuario          = User;
            Db.Insert(le);
            util.RestartSteps();
            Log.i("SaveLogEjercicio" ,"Se ha guardado un registro");
        }
    }

    private void SendBroadcast()
    {
        Intent intent = new Intent();
        intent.setAction(Paso);
        intent.putExtra (Steps,util.getSteps());
        sendBroadcast   (intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
