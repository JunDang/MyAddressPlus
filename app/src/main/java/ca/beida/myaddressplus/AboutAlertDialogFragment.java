package ca.beida.myaddressplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class AboutAlertDialogFragment extends DialogFragment {

    public static AboutAlertDialogFragment newInstance(int aboutResId) {
        AboutAlertDialogFragment adf = new AboutAlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("about_alert", aboutResId);
        adf.setArguments(bundle);
        return adf;
    }

   @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       this.setCancelable(true);
       //setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_TransparentYWCA);
       //setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent);
   }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        return new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_TransparentYWCA)
                              .setTitle("About...")
                              .setView(R.layout.activity_about_menu)
                              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      dismiss();
                                  }
                              }).create();

    }
}
