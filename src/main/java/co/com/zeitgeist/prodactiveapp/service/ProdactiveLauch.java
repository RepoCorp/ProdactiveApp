package co.com.zeitgeist.prodactiveapp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;

import co.com.zeitgeist.prodactiveapp.activity.LoginActivity;

public class ProdactiveLauch extends Service {
    public ProdactiveLauch() {
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

        Intent mainIntent = new Intent().setClass(this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(!sw)
            mainIntent.putExtra("isRestarting",true);
        startActivity(mainIntent);

        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    BroadcastReceiver receiverBoot= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            Intent service = new Intent(context, ProdactiveLauch.class);
            context.startService(service);


        }
    };*/
}




