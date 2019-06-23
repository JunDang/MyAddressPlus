package ca.beida.myaddressplus;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AddressListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = 80;// Menu.FIRST + 1; // It can be any number.
    private static final String TAG = "tag";
    String[] from;

    ListView listView;
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;
    TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG", "Jun Dang 666-888");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.mail);
        setContentView(R.layout.address_list);


        listView = findViewById(R.id.list);
        listView.setDividerHeight(2);
        emptyView = findViewById(R.id.empty);
        fillData();
        registerForContextMenu(listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.isItemChecked(position)) {
                    Intent i = new Intent(getApplicationContext(), AddressDetailActivity.class);
                    Uri addressUri = Uri.parse(MyAddressContentProvider.CONTENT_URI + "/" + id);
                    i.putExtra(MyAddressContentProvider.CONTENT_ITEM_TYPE, addressUri);

                    // Activity returns an result if called with startActivityForResult
                    startActivityForResult(i, ACTIVITY_EDIT);

                }
             }
        });

    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.about:
                displayAboutAlert();
                break;
            case R.id.insert:
                createNewAddress();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
       /* if(MyAddressContentProvider.cursorCotainsValue) {
            emptyView.setText("");
        } else {
            emptyView.setText("Currently there are no addresses.");
        }*/
        /*if(from.length>0) {
            emptyView.setText("");
        } else {
            emptyView.setText("Currently there are no addresses.");
        }*/
        super.onStart();

    }

    private void createNewAddress() {
        Intent i = new Intent(this, AddressDetailActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void fillData() {
        // Fields from the database (projection)
        // Must includpe the _id column for the adapter to work
        from = new String[] { MyAddressTableHandler.COLUMN_FIRSTNAME, MyAddressTableHandler.COLUMN_LASTNAME};

        // Fields on the UI to which we map
        int[] to = new int[] { R.id.firstnameLabel, R.id.lastNameLabel};

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.address_row, null, from, to, 0);

        listView.setAdapter(adapter);
        if(from.length>1) {
            emptyView.setText("");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Uri uri = Uri.parse(MyAddressContentProvider.CONTENT_URI + "/" + info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { MyAddressTableHandler.COLUMN_ID, MyAddressTableHandler.COLUMN_FIRSTNAME, MyAddressTableHandler.COLUMN_LASTNAME};
        CursorLoader cursorLoader = new CursorLoader(this, MyAddressContentProvider.CONTENT_URI, projection, null, null, null);


     /* if(MyAddressContentProvider.cursorCotainsValue) {
           emptyView.setText("");
        } else {
            emptyView.setText("Currently there are no addresses.");
        }*/
        if(from.length>0) {
            emptyView.setText("");
        } else {
            emptyView.setText("Currently there are no addresses.");
        }

        return cursorLoader;
    }

    //@Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    //@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    public void displayAboutAlert() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        AboutAlertDialogFragment aadf = AboutAlertDialogFragment.newInstance(R.string.about_message);
        aadf.show(transaction, TAG);
    }
}
