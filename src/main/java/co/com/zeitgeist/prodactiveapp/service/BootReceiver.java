package co.com.zeitgeist.prodactiveapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.com.zeitgeist.prodactiveapp.activity.SplashScreenActivity;

/**
 * Created by D on 22/09/2014.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("BootReceiver onReceived ", "Start");

        Intent myIntent = new Intent().setClass(context, SplashScreenActivity.class);
        //myIntent.putExtra("firstLaunch",true);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
        Log.i("BootReceiver onReceived ","Try Start activity");
    }
}
