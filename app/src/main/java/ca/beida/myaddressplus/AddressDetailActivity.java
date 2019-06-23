package ca.beida.myaddressplus;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddressDetailActivity extends AppCompatActivity {

    private Spinner mDesignation; //m means Java Member Variable
    private Spinner mProvince;
    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mAddressField;
    private EditText mCountryField;
    private EditText mPostalCodeField;
    private Uri addressUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_edit);
        Log.i("TAG", "Jun Dang 666-888");

        mDesignation = findViewById(R.id.designations);
        mProvince = findViewById(R.id.provinces);
        mFirstNameField = findViewById(R.id.firstnameID);
        mLastNameField = findViewById(R.id.lastnameID);
        mAddressField = findViewById(R.id.adressID);
        mCountryField = findViewById(R.id.countryID);
        mPostalCodeField = findViewById(R.id.postalcodeID);

        Button confirmBtn = (Button) findViewById(R.id.confirm_button);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            addressUri = extras.getParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE);
        } else {
            addressUri = (savedInstanceState == null) ?
                    null : (Uri) savedInstanceState.getParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE);
        }

        if (addressUri != null) {
            fillData(addressUri);
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (examinFieldsNotEmpty()) {
                    //setResult(RESULT_OK);

                    finish();
                }
            }
        });
    }

    private boolean examinFieldsNotEmpty() {

        if (TextUtils.isEmpty(mFirstNameField.getText().toString())) {
            Toast.makeText(this, "first name field cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(mLastNameField.getText().toString())) {
            Toast.makeText(this, "last name field cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(mAddressField.getText().toString())) {
            Toast.makeText(this, "address field cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(mCountryField.getText().toString())) {
            Toast.makeText(this, "country field cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(mPostalCodeField.getText().toString())) {
            Toast.makeText(this, "postal code field cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void fillData(Uri uri) {
        String[] projection = {MyAddressTableHandler.COLUMN_DESIGNATION,
                MyAddressTableHandler.COLUMN_FIRSTNAME,
                MyAddressTableHandler.COLUMN_LASTNAME, MyAddressTableHandler.COLUMN_ADDRESS,
                MyAddressTableHandler.COLUMN_PROVINCE, MyAddressTableHandler.COLUMN_COUNTRY,
                MyAddressTableHandler.COLUMN_POSTALCODE
        };
        Cursor cursor = getContentResolver().query(uri, projection, null, null);
        //check if actually have 1 matching row or record in the table
        //cursor object is a ResultSet or temporary table
        if (cursor != null) {
                    //This positions the query to the very first row of the cursor
                    cursor.moveToFirst();
                    String designation = cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_DESIGNATION));
                    //from the value of the designation field that is retrieved from the cursor
                    //set the index of the spinner to match.
                    for (int i = 0; i < mDesignation.getCount(); i++) {
                        String s = (String) mDesignation.getItemAtPosition(i);
                        if (s.equals(designation)) {
                            //sets the spinner index
                            mDesignation.setSelection(i);
                        }
            }
            // repeat the same steps for provinces
            String province = cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_PROVINCE));

            for (int i = 0; i < mProvince.getCount(); i++) {
                String s = (String) mProvince.getItemAtPosition(i);
                if (s.equals(province)) {
                    //sets the spinner index
                    mProvince.setSelection(i);
                }
            }

            mFirstNameField.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_FIRSTNAME)));
            mLastNameField.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_LASTNAME)));
            mAddressField.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_ADDRESS)));
            mCountryField.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_COUNTRY)));
            mPostalCodeField.setText(cursor.getString(cursor.getColumnIndexOrThrow(MyAddressTableHandler.COLUMN_POSTALCODE)));

            // Always close the cursor
            cursor.close();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String designation = (String)mDesignation.getSelectedItem();
        String province = (String)mProvince.getSelectedItem();
        String firstname = mFirstNameField.getText().toString();
        String lastname = mLastNameField.getText().toString();
        String address = mAddressField.getText().toString();
        String country = mCountryField.getText().toString();
        String postalcode = mPostalCodeField.getText().toString();

        Log.i("designationText", " " + designation);
        Log.i("provinceText", " " + province);
        Log.i("firstnameText", " " + firstname);
        Log.i("lastnameText", " " + lastname);
        Log.i("addressText", " " + address);
        Log.i("countryText", " " + country);
        Log.i("postalcodeText", " " + postalcode);

        //only save if the text fields are available;
        if (!examinFieldsNotEmpty()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MyAddressTableHandler.COLUMN_DESIGNATION, designation);
        values.put(MyAddressTableHandler.COLUMN_FIRSTNAME, firstname);
        values.put(MyAddressTableHandler.COLUMN_LASTNAME, lastname);
        values.put(MyAddressTableHandler.COLUMN_ADDRESS, address);
        values.put(MyAddressTableHandler.COLUMN_PROVINCE, province);
        values.put(MyAddressTableHandler.COLUMN_COUNTRY, country);
        values.put(MyAddressTableHandler.COLUMN_POSTALCODE, postalcode);



        if(addressUri == null) {
            addressUri = getContentResolver().insert(MyAddressContentProvider.CONTENT_URI, values);
            Log.i("addressuri", " " + addressUri);
        } else {
            //update ToDo (existing TASK);
            getContentResolver().update(addressUri, values, null, null);
        }

    }
}
