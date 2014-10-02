package co.com.zeitgeist.prodactiveapp.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;

import co.com.zeitgeist.prodactiveapp.activity.LoginActivity;
import co.com.zeitgeist.prodactiveapp.activity.PedometroActivity;

public class ProdactiveLauchService extends Service {

    public static final String TAG = "co.com.zeitgeist.prodactiveapp.prodactivelaunch";

    public ProdactiveLauchService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean sw= false;
        try{
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("firstLaunch")) {
                    sw = extras.getBoolean("firstLaunch", false);
                    Log.i("ProdactiveLaunc OnStarCommand","value:" + sw);
                    // TODO: Do something with the value of isNew.
                }
            }
        }catch (NullPointerException ex){
            Log.e("ProdactiveLaunc OnStarCommand","the intent not contain extras");
        }
        //Intent mainIntent = new Intent().setClass(this, LoginActivity.class);
        Intent mainIntent = new Intent().setClass(this, PedometroActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(!sw)
            mainIntent.putExtra("isRestarting",true);
        startActivity(mainIntent);



        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public final IBinder       mBinder = new LaunchBinder();

    public class LaunchBinder extends Binder {
        public ProdactiveLauchService getService() {
            return ProdactiveLauchService.this;
        }
    }



}




