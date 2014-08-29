package co.com.zeitgeist.prodactiveapp.database;

import android.database.Cursor;

/**
 * Created by D on 22/08/2014.
 */
public interface ITable {
    public String CreateTableSQL();

    Insertable SerializeItem(Cursor c);
    String SelectAll();

    //Insertable SerializeItem(Cursor c);
}