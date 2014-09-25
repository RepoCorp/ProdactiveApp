package co.com.zeitgeist.prodactiveapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by D on 22/08/2014.
 */
public class DbHelper extends SQLiteOpenHelper {


    private static final String DB_NAME           = "prodactive.db";
    private static final int    DB_SCHEME_VERSION = 2;

    private final SQLiteDatabase DB;

    private static DbHelper Instance;
    private static Object mutex = new Object();

    public static DbHelper getInstance(Context context)
    {
        if(Instance==null)
        {
            synchronized (mutex)
            {
                if(Instance==null)
                {
                    Instance = new DbHelper(context);
                }
            }
        }
        return Instance;
    }

    private DbHelper(Context context)
    {
        super(context, DB_NAME, null ,DB_SCHEME_VERSION);
        DB = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(new TablaPersona().CreateTableSQL());
        db.execSQL(new TablaLogEjercicio().CreateTableSQL());
        db.execSQL(new TablaLogDiario().CreateTableSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TablaPersona.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TablaLogEjercicio.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TablaLogDiario.TABLE_NAME);

    }

    /*public void Select(Insertable item,String [] projection , String selection,String[] selectionArgs,String sortOrder)
    {
        Cursor c= DB.query(item.GetTableName(),projection,selection,selectionArgs,null,null,sortOrder);

    }*/

    public ArrayList<Insertable> Select(String query,ITable table)
    {
        ArrayList<Insertable> elm = new ArrayList<Insertable>();
        try{
            Cursor c= DB.rawQuery(query,null);
            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        elm.add(table.SerializeItem(c));
                    }while (c.moveToNext());
                }
                c.close();
            }

        }catch(Exception ex )
        {
            Log.e("Select",(ex.getMessage()!=null?ex.getMessage():""));
        }
        return elm;
    }

    public boolean Insert(Insertable item)
    {

        long result=DB.insert(item.GetTableName(),null,item.GetContentValues());
        if(result>0)
        {
            Log.i("DBHelper","Registro guardado en la base de datos");
            return true;
        }


        return false;
    }

    public boolean Update(Insertable item)
    {
        long result = DB.update(item.GetTableName(),item.GetContentValues(),item.GetWhereClause(),null);
        return (result>0);
    }

    public boolean Delete(Insertable item)
    {
        long result= DB.delete(item.GetTableName(),item.GetWhereClause(),null);
        return (result>0);
    }
}