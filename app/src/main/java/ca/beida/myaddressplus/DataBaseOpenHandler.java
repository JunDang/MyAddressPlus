package ca.beida.myaddressplus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "addresstable.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseOpenHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        MyAddressTableHandler.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MyAddressTableHandler.onUpgrade(db, oldVersion, newVersion);

    }
}
