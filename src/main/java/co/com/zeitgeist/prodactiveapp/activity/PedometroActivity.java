package co.com.zeitgeist.prodactiveapp.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;

import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import co.com.zeitgeist.prodactiveapp.R;
import co.com.zeitgeist.prodactiveapp.database.DbHelper;
import co.com.zeitgeist.prodactiveapp.database.Insertable;
import co.com.zeitgeist.prodactiveapp.database.TablaLogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.LogDiario;
import co.com.zeitgeist.prodactiveapp.database.model.LogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.ServiceResponse;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;
import co.com.zeitgeist.prodactiveapp.service.RestService;
import co.com.zeitgeist.prodactiveapp.service.StepService;


public class PedometroActivity extends Activity {

    public final static  String MessageToStepService="co.com.zeitgeist.prodactive.MESSAGE_TO_STEPSERVICE";
    public final static String RestartCounterOnStepService="co.com.zeitgeist.prodactive.RESTAR_COUNTER_ON_STEPSERVICE";
    public final static String InitProdactive="co.com.zeitgeist.prodactive.INIT_PRODACTIVE";
    public final static String IsRestarted ="restarted";

    private Activity activity;
    //Receiver broadcastReceiver;
    private TextView pasos;
    private TextView calorias;
    private Date     lastReport;
    private Utils    utils;
    private DbHelper Db;
    private ComunicationStepService s;
    //private String User="";

    private final Object  obj           = new Object();
    private boolean sw            = false;
    private final Object  mutex         = new Object();
    private boolean sw2           = false;
    private Double  Calories      = 0.0;
    private double  StepLength    = 0;
    private double  BodyWeight    = 0;


    private final static double METRIC_RUNNING_FACTOR   = 1.02784823;
    //private final static double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    private final static double METRIC_WALKING_FACTOR   = 0.708;
    //private final static double IMPERIAL_WALKING_FACTOR = 0.517;

    private DecimalFormatSymbols decimalFormatSymbols     = new DecimalFormatSymbols();
    private DecimalFormat decimalFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

        moveTaskToBack(getIntent().getBooleanExtra(IsRestarted,false));

        setContentView(R.layout.activity_pedometro);

        activity = this;
        utils    = Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(getBaseContext()));
        Db       = new DbHelper(activity);

        loadContent();
        GetStepLength(utils.GetSex(),utils.GetHeight());
        BodyWeight= utils.GetWeight();



        IntentFilter filter = new IntentFilter();

        filter.addAction(StepService.Paso);
        registerReceiver(receiver , filter);



        //startService(msgStepIntent);
        Intent msgStepIntent = new Intent(this,ComunicationStepService.class);
        startService(msgStepIntent);
        bindStepService();
        //bindService(msgStepIntent,mConnection,Context.BIND_AUTO_CREATE);


       // bindStepService();
        if(isMyServiceRunning(StepService.class)){
            Toast.makeText(this,"Service was Running",Toast.LENGTH_LONG).show();
        }else
        {
            Intent stepIntent = new Intent(this, StepService.class);
            startService(stepIntent);
        }


        EnvioLogServicio els= new EnvioLogServicio();
            Timer timer = new Timer();
            Integer timeOutReport = 5 * 60 * 1000;
            timer.schedule(els, timeOutReport, timeOutReport);


        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
        //s.SendInitApp();
        }catch(Exception ex)
        {
            Log.e("ErrorOnCreateApp",ex.getMessage());
        }
    }
    @Override
    protected void onResume()
    {
        //User=utils.GetUser();
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {

        utils.UpdateLastStep(utils.GetStepsFromLastReport());
        unregisterReceiver(receiver);
        unbindStepService();
        SaveLogEjercicio();
        Log.i("PedometroActivity onDestroy","se ha cerrado la aplicacion");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pedometro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            if(intent.getAction().equals(StepService.Paso))
            {
                if(!sw)
                {
                    synchronized (obj)
                    {
                        if(!sw)
                        {
                            sw = true;
                            try{

                                int contpasos=intent.getIntExtra(StepService.Steps, 0);
                                utils.setSteps(contpasos);
                                //actualizo interfaz gráfica
                                Calories = contpasos *  (BodyWeight * METRIC_WALKING_FACTOR //(mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                                        // Distance:
                                        * StepLength // centimeters
                                        / 100000.0); // centimeters/kilometer

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        pasos.setText(utils.getSteps().toString());
                                        //pasos.setText(s.getSteps().toString());
                                        calorias.setText(decimalFormat.format(Calories));
                                        if(lastReport==null)
                                            lastReport = new Date(SystemClock.elapsedRealtime());
                                        //verifico si debo reportar
                                        Date d=new Date(SystemClock.elapsedRealtime());
                                        //if((d.getTime()-lastReport.getTime())>(5*60*1000))
                                        if((d.getTime()-lastReport.getTime())>(30000))
                                        {
                                            if(utils.IsSameDay())
                                            {
                                                SaveLogEjercicio();
                                            }
                                            else
                                            {
                                                SaveLogEjercicio();
                                                SaveLogDiario();
                                                utils.SetCurrentDate(new Date());
                                            }
                                            lastReport = new Date(SystemClock.elapsedRealtime());
                                        }
                                    }
                                });
                            }
                            catch (Exception e){
                                Log.e("SendReporte PedometroActivity",e.getMessage());
                            }
                            finally {
                                sw=false;
                            }

                        }
                    }
                }
            }

        }
    };


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                    Integer cont    = utils.GetStepsFromLastReport();

                    if(cont>0){
                        LogEjercicio le     = new LogEjercicio();
                        le.Velocidad        = (double) 0;
                        le.Conteo           = cont;
                        le.Ubicacion        = "lat=lon=";
                        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HHmmss");
                        le.Fecha            = sdf.format(fecha);
                        le.Usuario          = utils.GetUser();
                        Db.Insert(le);
                        //actualizo el valor en el servicio
                        s.SendDataToService(cont);
                        utils.UpdateLastStep(cont);
                        Log.i("SaveLogEjercicio" ,"Se ha guardado un registro");

                    }
                    sw2=false;
                }
            }
        }

    }

    private void SaveLogDiario() {
        Date    fecha   = utils.GetCurrentDate();
        Integer cont    = utils.getSteps();
        if(cont>0){
            LogDiario le     = new LogDiario();
            le.Velocidad        = (double) 0;
            le.Conteo           = cont;
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            le.Fecha            = sdf.format(fecha);
            le.Usuario          = utils.GetUser();
            Db.Insert(le);
            utils.RestartSteps();
            s.RestartCounterOnService();
            Log.i("SaveLogEjercicio" ,"Se ha guardado un registro");
            }
    }




    private void GetStepLength(String sexo, double estatura)
    {
        if (sexo.equals("M"))
        {
            if (estatura != 0)
                StepLength = estatura*0.415;
            else
                StepLength = 78;
        }
        else
        {
            if (estatura == 0)
                StepLength = estatura*0.413;
            else
                StepLength = 70;
        }
    }

    private void loadContent() {
        pasos    = (TextView) findViewById(R.id.pasosView);
        calorias = (TextView) findViewById(R.id.caloriasView);

        TextView userdata =(TextView) findViewById(R.id.user_data);
        userdata.setText(" Bienvenido "+utils.GetUser());
    }

    private void bindStepService() {
        Log.i("BindStepService", "[SERVICE] Bind");
        //bindService(new Intent(this,ComunicationStepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
        Intent msgStepIntent = new Intent(getApplicationContext(),ComunicationStepService.class);
        try{
            bindService(msgStepIntent, mConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception ex)
        {
            Log.e("Error Bind Service",ex.getMessage());
        }

    }
    private void unbindStepService() {
        Log.i("UnBindStepService", "[SERVICE] Unbind");
        if(mConnection!=null)
            unbindService(mConnection);
    }

    /**
     * Esta clase se encarga de recibir la notificacion de pasos desde el servicio StepService
     */

    /**
     * Revisa constantemente la base de datos de LogEjercicios y lo reporta al servidor
     */
    private class EnvioLogServicio extends TimerTask
    {
        @Override
        public void run() {

            Log.i("EnvioLogServicio","Se ha iniciado reporte");
            TablaLogEjercicio t= new TablaLogEjercicio();
            for (Insertable insertable : Db.Select(t.SelectAll(), t)) {
                LogEjercicio le = (LogEjercicio) insertable;
                if (le.Usuario.equals("")) {
                    le.Usuario = utils.GetUser();
                }
                final String url = "http://prodactive.co/api/LogEjercicio/" + le.Usuario + "/" + le.Fecha + "/lat=lon=/" + le.Conteo + "/0?format=json";
                //AsyncTask<String,Void,ServiceResponse> task
                try {

                    RestService<ServiceResponse> sr = new RestService<ServiceResponse>();
                    ServiceResponse response = sr.Send(url, new ServiceResponse());
                    if (response.State) {
                        Db.Delete(le);
                    }
                } catch (Exception ex) {
                    Log.e("EnvioLogServicio", ex.getMessage());
                }

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new RestServiceAsyncTask(activity,true).execute(url);
                    }
                });*/
            }
        }
    }

    //objeto que permite comunicar localmente con el servicio ComunicacionStepService, para enviar los
    //broadcast al StepService
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            ComunicationStepService.LocalBinder b = (ComunicationStepService.LocalBinder) binder;
            s = b.getService();
            Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
            s.SendInitApp();
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };


    public static class ComunicationStepService extends Service
    {
        public ComunicationStepService()
        {

        }

        private final IBinder       mBinder = new LocalBinder();

        public class LocalBinder extends Binder {
            public ComunicationStepService getService() {
                return ComunicationStepService.this;
            }
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i("ComunicationStepService", "Received start id " + startId + ": " + intent);
            // We want this service to continue running until it is explicitly
            // stopped, so return sticky.
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }

        public void RestartCounterOnService()
        {
            Intent bcIntent = new Intent();
            bcIntent.setAction(RestartCounterOnStepService);
            sendBroadcast(bcIntent);
        }

        public void SendDataToService(Integer value)
        {
            Intent bcIntent = new Intent();
            bcIntent.setAction(MessageToStepService);
            bcIntent.putExtra(StepService.UpdatedSteps,value);
            sendBroadcast(bcIntent);
        }
        public void SendInitApp()
        {
            Intent bcIntent = new Intent();
            bcIntent.setAction(InitProdactive);
            sendBroadcast(bcIntent);
        }
    }

}


