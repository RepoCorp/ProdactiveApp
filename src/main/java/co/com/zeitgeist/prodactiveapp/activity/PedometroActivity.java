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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import co.com.zeitgeist.prodactiveapp.R;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;
import co.com.zeitgeist.prodactiveapp.service.StepService;
import co.com.zeitgeist.prodactiveapp.service.UploadService;

public class PedometroActivity extends Activity {

    public final static String MessageToStepService="co.com.zeitgeist.prodactive.MESSAGE_TO_STEPSERVICE";
    public final static String RestartCounterOnStepService="co.com.zeitgeist.prodactive.RESTAR_COUNTER_ON_STEPSERVICE";
    public final static String InitProdactive="co.com.zeitgeist.prodactive.INIT_PRODACTIVE";
    public final static String IsRestarted ="restarted";

    private Activity activity;
    //Receiver broadcastReceiver;
    private TextView pasos;
    private TextView calorias;
    //private Date     lastReport;
    private Utils    utils;
    //private DbHelper Db;
    private ComunicationStepService s;
    //private String User="";

    private final Object  obj     = new Object();
    private boolean sw            = false;
    //private final Object  mutex   = new Object();
    //private boolean sw2           = false;
    private Double  Calories      = 0.0;
    private double  StepLength    = 0;
    private double  BodyWeight    = 0;
    private String  User          = "";


    //private final static double METRIC_RUNNING_FACTOR   = 1.02784823;
    //private final static double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    private final static double METRIC_WALKING_FACTOR   = 0.708;
    //private final static double IMPERIAL_WALKING_FACTOR = 0.517;

    private DecimalFormatSymbols decimalFormatSymbols     = new DecimalFormatSymbols();
    private DecimalFormat decimalFormat;

    UploadService uploadService=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{

        moveTaskToBack(getIntent().getBooleanExtra(IsRestarted,false));

        setContentView(R.layout.activity_pedometro);

        activity = this;
        utils    = Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        //Db       = DbHelper.getInstance(activity);

        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);


        loadContent();
        GetStepLength(utils.GetSex(),utils.GetHeight());
        BodyWeight= utils.GetWeight();


        IntentFilter filter = new IntentFilter();

        filter.addAction(StepService.Paso);
        registerReceiver(receiver , filter);

        //startService(msgStepIntent);
        Intent intent1 = new Intent(this,ComunicationStepService.class);
        startService(intent1);

        bindComunicationService();
        //bindService(msgStepIntent,mConnection,Context.BIND_AUTO_CREATE);

       // bindStepService();
        /*if(isMyServiceRunning(StepService.class)){
            Toast.makeText(this,"Service was Running",Toast.LENGTH_LONG).show();
        }else
        {
            Intent stepIntent = new Intent(this, StepService.class);
            stepIntent.putExtra("User",User);
            startService(stepIntent);
        }*/

        /*if(isMyServiceRunning(UploadService.class)){
            Toast.makeText(this,"Service was Running",Toast.LENGTH_LONG).show();
        }else
        {
            Intent stepIntent = new Intent(this, UploadService.class);
            stepIntent.putExtra("User",User);
            startService(stepIntent);
        }*/

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
        unregisterReceiver(receiver);
        unbindStepService();
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
                                Calories = GetCalories(contpasos);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pasos.setText(utils.getSteps().toString());
                                        calorias.setText(decimalFormat.format(Calories));
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

    private double GetCalories(int contpasos){
        return contpasos *  (BodyWeight * METRIC_WALKING_FACTOR //(mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                // Distance:
                * StepLength // centimeters
                / 100000.0); // centimeters/kilometer

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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



        pasos.setText(utils.getSteps().toString());
        calorias.setText(decimalFormat.format(GetCalories(utils.getSteps())));

        TextView userdata =(TextView) findViewById(R.id.user_data);
        User=utils.GetUser();
        userdata.setText(" Bienvenido "+User);
    }

    private void bindComunicationService() {

        //bindService(new Intent(this,ComunicationStepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
        Intent msgStepIntent = new Intent(getApplicationContext(),ComunicationStepService.class);
        try{
            bindService(msgStepIntent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i("BindComunicationService", "[SERVICE Comunication] Bind");
        }catch (Exception ex)
        {
            Log.e("Error Bind Service",ex.getMessage());
        }

        try{
            Intent stepIntent= new Intent(getApplicationContext(),UploadService.class);
            stepIntent.putExtra("User",User);
            bindService(stepIntent,mConnetionUpload,Context.BIND_AUTO_CREATE);
            Log.i("BindUploadService", "[SERVICE Upload] Bind");
        }
        catch(Exception ex)
        {

        }


    }
    private void unbindStepService() {
        Log.i("UnBindStepService", "[SERVICE Comunication] Unbind");
        if(mConnection!=null)
            unbindService(mConnection);
        if(mConnetionUpload !=null)
            unbindService(mConnetionUpload);
    }

    /**
     * Esta clase se encarga de recibir la notificacion de pasos desde el servicio StepService
     */

    /**
     * Revisa constantemente la base de datos de LogEjercicios y lo reporta al servidor
     */
  /*
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new RestServiceAsyncTask(activity,true).execute(url);
                    }
                });
            }
        }
    }
*/
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

    private ServiceConnection mConnetionUpload = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            UploadService.UploadBinder b= (UploadService.UploadBinder) iBinder;
            uploadService= b.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            uploadService=null;
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


