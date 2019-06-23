package ca.beida.myaddressplus;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

public class MyAddressContentProvider extends ContentProvider {

    public static boolean cursorCotainsValue = false;

    //database
    private DataBaseOpenHandler databaseHandler;
    //used for the UriMatcher
    private static final int ADDRESSES = 10;
    private static final int ADDRESS_ID = 20;
    private static final String AUTHORITY = "ca.beida.myaddressplus.MyAddressContentProvider";
    private static final String BASE_PATH = "addresses";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/addresses";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/address";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ADDRESSES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ADDRESS_ID);
    }
    @Override
    public boolean onCreate() {
        databaseHandler = new DataBaseOpenHandler(getContext());
        return false;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,  String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(MyAddressTableHandler.TABLE_ADDRESS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ADDRESSES:
                break;
            case ADDRESS_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(MyAddressTableHandler.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        if (cursor.getCount() > 0)
            cursorCotainsValue = true;

        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = databaseHandler.getWritableDatabase();
        //int rowsDeleted = 0;
        long id = 0;

        switch (uriType) {
            case ADDRESSES: //insert to table without where clause
                id = sqlDB.insert(MyAddressTableHandler.TABLE_ADDRESS, null, values);
                Log.i("insert method" , "id: " + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = databaseHandler.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case ADDRESSES:
                rowsDeleted = sqlDB.delete(MyAddressTableHandler.TABLE_ADDRESS, selection, selectionArgs);
                break;
            case ADDRESS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(MyAddressTableHandler.TABLE_ADDRESS, MyAddressTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(MyAddressTableHandler.TABLE_ADDRESS, MyAddressTableHandler.COLUMN_ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = databaseHandler.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case ADDRESSES:
                rowsUpdated = sqlDB.update(MyAddressTableHandler.TABLE_ADDRESS, values, selection, selectionArgs);
                break;
            case ADDRESS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MyAddressTableHandler.TABLE_ADDRESS, values,
                            MyAddressTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(MyAddressTableHandler.TABLE_ADDRESS,
                            values, MyAddressTableHandler.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { MyAddressTableHandler.COLUMN_ID, MyAddressTableHandler.COLUMN_DESIGNATION,
               MyAddressTableHandler.COLUMN_FIRSTNAME,
                MyAddressTableHandler.COLUMN_LASTNAME, MyAddressTableHandler.COLUMN_ADDRESS,
                MyAddressTableHandler.COLUMN_PROVINCE, MyAddressTableHandler.COLUMN_COUNTRY,
                MyAddressTableHandler.COLUMN_POSTALCODE};

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
