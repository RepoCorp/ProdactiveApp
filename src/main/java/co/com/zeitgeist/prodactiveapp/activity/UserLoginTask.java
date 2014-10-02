package co.com.zeitgeist.prodactiveapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import co.com.zeitgeist.prodactiveapp.R;
import co.com.zeitgeist.prodactiveapp.config.Preferences;
import co.com.zeitgeist.prodactiveapp.database.DbHelper;
import co.com.zeitgeist.prodactiveapp.database.model.LoginResponse;
import co.com.zeitgeist.prodactiveapp.helpers.RestService;
import co.com.zeitgeist.prodactiveapp.helpers.Utils;
import co.com.zeitgeist.prodactiveapp.service.ProdactiveLauchService;
import co.com.zeitgeist.prodactiveapp.service.StepService;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mUser;
    private final String mPassword;
    final LoginActivity activity;
    Utils util=null;

    UserLoginTask(String user, String password,LoginActivity activity) {
        mUser            = user;
        mPassword        = password;
        this.activity    = activity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.
        //aqui va el logueo

        //Preferences p = Preferences.GetInstance(activity.getPreferences(Context.MODE_PRIVATE));
        util= Utils.GetInstance(PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()));
        String[]    datos = util.GetUserPass();
        if(!datos[0].isEmpty() && !datos[1].isEmpty())
        {
            if(datos[0].equals(mUser) && datos[1].equals(mPassword))
            {
                return true;
            }
        }

        String url = "http://prodactive.co/api/login/"+mUser+"/"+mPassword+"?format=json";
        RestService<LoginResponse> r= new RestService<LoginResponse>();
        try {
            LoginResponse response = r.Send(url, new LoginResponse());
            if (response.State) {
                DbHelper h = DbHelper.getInstance(activity);
                try{
                    //h.Insert(response.Persona);
                }catch (Exception ex){
                    Log.e("Login Insert Persona", ex.getMessage());
                }
                util.PutWeight(response.Persona.Peso.intValue());
                util.PutHeight(response.Persona.Estatura.floatValue());
                util.PutSex(response.Persona.Sexo);
                if(!mPassword.equals(""))
                     util.SaveUser(mUser, mPassword);
            } else
                return false;
        }catch (Exception ex){
            return false;
        }
        //guardar localmente los datos, para evitar ir al servidor la proxima vez
        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        activity.mAuthTask = null;
        activity.showProgress(false);

        if (success) {
            Intent intent = new Intent(activity, PedometroActivity.class);
            intent.putExtra("User",mUser);
            intent.putExtra("Password",mPassword);
            activity.startActivity(intent);

            if(!activity.isMyServiceRunning(StepService.class)){
                Intent intent1 = new Intent(activity,StepService.class);
                intent.putExtra("User",mUser);
                intent.putExtra("Password",mPassword);
                activity.startService(intent1);
            }

            //intent.putExtra(PedometroActivity.IsRestarted,true);
            //activity.startActivity(intent);
            activity.finish();
            //finish();
            //cargo la otra actividad
        } else {
            activity.mPasswordView.setError(activity.getString(R.string.error_incorrect_password));
            activity.mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        activity.mAuthTask = null;
        activity.showProgress(false);
    }
}