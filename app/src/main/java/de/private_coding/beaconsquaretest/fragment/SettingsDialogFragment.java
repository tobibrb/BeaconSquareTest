package de.private_coding.beaconsquaretest.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
public class SettingsDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events

    public SettingsDialogFragment() {}


    public static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.settings_dialog, null);
        final EditText settingsText = (EditText) v.findViewById(R.id.testTime_Text);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int time = preferences.getInt("testTimeKey", 10);

        settingsText.setHint(time + " seconds");

        builder.setMessage("Choose length for Test duration in seconds.");
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                String text = settingsText.getText().toString();
                final int time;

                if (text.isEmpty()) {
                    time = preferences.getInt("testTimeKey", 10);
                } else time = Integer.parseInt(text);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();

                editor.putInt("testTimeKey", time);
                editor.apply();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Test duration is set on " + time + " seconds.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return builder.create();
    }
}
