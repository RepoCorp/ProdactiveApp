package co.com.zeitgeist.prodactiveapp.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ProdactiveLauch extends Service {
    public ProdactiveLauch() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent mainIntent = new Intent().setClass(this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    BroadcastReceiver receiverBoot= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Start Service On Boot Start Up
            Intent service = new Intent(context, ProdactiveLauch.class);
            context.startService(service);

            //Start App On Boot Start Up
            //Intent App = new Intent(context, SplashScreenActivity.class);
            //App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(App);

        }
    };
}


