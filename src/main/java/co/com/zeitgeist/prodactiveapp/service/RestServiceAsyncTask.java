package co.com.zeitgeist.prodactiveapp.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import co.com.zeitgeist.prodactiveapp.R;
import co.com.zeitgeist.prodactiveapp.database.model.ServiceResponse;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;

/**
 * Created by D on 23/08/2014.
 * por el momento no se esta usando
 */
public class RestServiceAsyncTask extends AsyncTask<String, Void,ServiceResponse> {

    Context context;
    boolean showToast;
    public RestServiceAsyncTask(Context context,boolean showToast)
    {
        this.context   = context;
        this.showToast = showToast;
    }
    @Override
    protected ServiceResponse doInBackground(String... strings) {
        Log.i("RestServiceASyncTask","envio de reporte final");
        RestService<ServiceResponse> sr= new RestService<ServiceResponse>();
        return sr.Send(strings[0],new ServiceResponse());
    }

    @Override
    protected void onPostExecute(ServiceResponse serviceResponse) {
        if(serviceResponse.State)
            Log.e("Reporte Destroy","se ha enviado el reporte antes de cerrar la aplicación.");
        if(showToast)
            Toast.makeText(context, context.getString(R.string.message_report_data), Toast.LENGTH_LONG);

    }
}
