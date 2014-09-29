package co.com.zeitgeist.prodactiveapp.helpers;

import android.os.AsyncTask;
import android.util.Log;

import co.com.zeitgeist.prodactiveapp.database.DbHelper;
import co.com.zeitgeist.prodactiveapp.database.model.LogEjercicio;
import co.com.zeitgeist.prodactiveapp.database.model.ServiceResponse;

/**
 * Created by D on 23/08/2014.
 * por el momento no se esta usando
 */

class RestServiceAsyncTask extends AsyncTask<LogEjercicio, Void,LogEjercicio> {

    private final DbHelper Db;
    public RestServiceAsyncTask(DbHelper db)
    {
        Db= db;
    }


    @Override
    protected LogEjercicio doInBackground(LogEjercicio... logEjercicios) {

        try{
            LogEjercicio le= logEjercicios[0];
            final String url = "http://prodactive.co/api/LogEjercicio/" + le.Usuario + "/" + le.Fecha + "/lat=lon=/" + le.Conteo + "/0?format=json";
            Log.i("RestServiceASyncTask","envio de reporte final");
            RestService<ServiceResponse> sr= new RestService<ServiceResponse>();
            ServiceResponse res=sr.Send(url,new ServiceResponse());

            if(res.State)
                return le;

        }catch(Exception ex)
        {
            Log.e("RestServiceAsyncTask doInBackground",ex.getMessage());
            LogEjercicio response = new LogEjercicio();
            response.Id           = -1;
            return response;
        }
        return null;
    }

    @Override
    protected void onPostExecute(LogEjercicio logEjercicio) {
        try{
            if(!logEjercicio.Id.equals(-1))
                Db.Delete(logEjercicio);
        }
        catch(Exception ex){
            Log.e("RestServiceAsyncTask onPostExecute",ex.getMessage());
        }


    }
}
