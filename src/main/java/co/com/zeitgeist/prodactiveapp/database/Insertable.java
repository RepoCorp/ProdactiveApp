package co.com.zeitgeist.prodactiveapp.database;

import android.content.ContentValues;

/**
 * Created by D on 22/08/2014.
 */
public interface Insertable {

    public ContentValues GetContentValues();
    public String        GetTableName();
    public String        GetWhereClause();
}