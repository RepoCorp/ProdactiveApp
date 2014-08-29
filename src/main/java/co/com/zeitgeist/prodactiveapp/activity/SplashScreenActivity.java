package co.com.zeitgeist.prodactiveapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import co.com.zeitgeist.prodactiveapp.R;
import co.com.zeitgeist.prodactiveapp.database.DbHelper;
import co.com.zeitgeist.prodactiveapp.database.Insertable;
import co.com.zeitgeist.prodactiveapp.database.TablaLogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.LogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.ServiceResponse;
import co.com.zeitgeist.prodactiveapp.service.RestServiceAsyncTask;

public class SplashScreenActivity extends Activity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        /*
            LogEjercicio l= (LogEjercicio) lst.next();
            String url="http://prodactive.co/api/LogEjercicio/"+l.Usuario+"/"+l.Fecha+"/lat=lon=/"+l.Conteo+"/0?format=json";
            RestServiceAsyncTask r= new RestServiceAsyncTask();
            AsyncTask<String,Void,ServiceResponse> task=r.execute(url);
                ServiceResponse sr = task.get();
                if(sr.State)
                    h.Delete(l);
            */


        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // Start the next activity
                Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, LoginActivity.class);
                startActivity(mainIntent);

                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}
