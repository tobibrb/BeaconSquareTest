package de.private_coding.beaconsquaretest.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import de.private_coding.beaconsquaretest.R;
import de.private_coding.beaconsquaretest.layout.CustomTable;
import de.private_coding.beaconsquaretest.listener.BeaconListener;

import static java.lang.Thread.sleep;

/**
 * Created by Bartz, Tobias on 10.11.2015 at 19:14.
 */
public class CaptureTask extends AsyncTask<Void,Void,Void> {

    private String rowColumn;
    private BeaconListener listener;
    private ProgressDialog dialog;
    private Context context;
    private ImageButton button;


    public CaptureTask(Context context, String rowColumn) {
        super();
        this.rowColumn = rowColumn;
        this.listener = BeaconListener.getInstance();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        button = CustomTable.getImageButton(rowColumn);
        button.setImageResource(R.drawable.yellow);
        dialog = ProgressDialog.show(context, "",
                "Doing stuff and things. Please wait...", false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        listener.setRowColumn(rowColumn);
        listener.setCapture(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        int time = preferences.getInt("testTimeKey", 10);

        try {
            sleep(time*1000); //wird hier die Messdauer eingetragen???
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listener.setCapture(false);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        button.setImageResource(R.drawable.green);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
