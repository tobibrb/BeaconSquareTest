package de.private_coding.beaconsquaretest.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.private_coding.beaconsquaretest.R;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 18:00.
 */
public class SettingsDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    SettingsFragmentListener mListener;

    public SettingsDialogFragment() {}


    public static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

    // Override the Fragment.onAttach() method to instantiate the SizeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SettingsFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.settings_dialog, null);
        final EditText settingsText = (EditText) v.findViewById(R.id.testTime_Text);

        builder.setMessage("Choose length for Test Time");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onSettingsDialogPositiveClick(SettingsDialogFragment.this, Integer.parseInt(settingsText.getText().toString()));

            }
        });
        builder.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                mListener.onSettingsDialogNegativeClick(SettingsDialogFragment.this);
            }
        });
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SettingsFragmentListener {
        void onSettingsDialogPositiveClick(DialogFragment dialog, int time);

        void onSettingsDialogNegativeClick(DialogFragment dialog);
    }
}
