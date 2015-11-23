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
import android.widget.Toast;

import de.private_coding.beaconsquaretest.R;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 18:00.
 */
public class SizeDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    SizeFragmentListener mListener;

    public SizeDialogFragment() {
    }


    public static SizeDialogFragment newInstance() {
        return new SizeDialogFragment();
    }

    // Override the Fragment.onAttach() method to instantiate the SizeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SizeFragmentListener) activity;
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
        View v = inflater.inflate(R.layout.dimension_dialog, null);
        final EditText heightText = (EditText) v.findViewById(R.id.height_text);
        final EditText widthText = (EditText) v.findViewById(R.id.width_text);
        builder.setMessage("Choose dimensions for test");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(SizeDialogFragment.this,
                        Integer.parseInt(heightText.getText().toString()),
                        Integer.parseInt(widthText.getText().toString()));
            }
        });
        builder.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(SizeDialogFragment.this);
            }
        });
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SizeFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog, int height, int width);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}
