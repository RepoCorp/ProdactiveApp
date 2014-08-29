package co.com.zeitgeist.prodactiveapp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import co.com.zeitgeist.prodactiveapp.activity.PedometroActivity;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;

/**
 * Created by D on 22/08/2014.
 */
public class StepService extends Service implements SensorEventListener{

    public final static String Steps       = "Steps";
    public final static String UpdatedSteps       = "UpdatedSteps";

    private final static String TAG     = "StepDetector";

    static StepService s;
    public static String Paso="co.com.zeitgeist.prodactive.PASO";

    SensorManager sensorManager;
    Sensor        sensor;


    //private final IBinder binderService = new LocalBinder();


    private float   mLimit = 10;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;

    static Utils util;


    public StepService()
    {

        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));


    }

    BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PedometroActivity.MessageToStepService))
            {
                int value=intent.getIntExtra(UpdatedSteps,util.GetStepsFromLastReport());
                util.UpdateLastStep(value);
            }
            if(intent.getAction().equals(PedometroActivity.InitProdactive))
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
        Context ctx = getApplicationContext();
        util=Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(ctx));

        //receiver   = new ComunicationStepServiceReceiver();

        IntentFilter filter = new IntentFilter();

        filter.addAction(PedometroActivity.MessageToStepService);
        registerReceiver(receiver, filter);
        s=this;
    }

    public void onDestroy()
    {
        unregisterReceiver(receiver);
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

    private void SendBroadcast()
    {
        Intent bcIntent = new Intent();

        bcIntent.setAction(Paso);
        bcIntent.putExtra (Steps,util.getSteps());
        sendBroadcast     (bcIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
