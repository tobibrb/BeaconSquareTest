package de.private_coding.beaconsquaretest.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;

import java.util.Date;

import de.private_coding.beaconsquaretest.R;
import de.private_coding.beaconsquaretest.listener.BeaconListener;
import de.private_coding.beaconsquaretest.listener.Table;

import static java.lang.Thread.sleep;

/**
 * Created by Bartz, Tobias on 10.11.2015 at 19:14.
 */
public class CaptureTask extends AsyncTask<Void,Void,Void> {

    private Table table;
    private BeaconListener listener;
    private ProgressDialog dialog;
    private Context context;
    private ImageButton button;


    public CaptureTask(Context context, ImageButton button, Table table, BeaconListener listener) {
        super();
        this.table = table;
        this.listener = listener;
        this.context = context;
        this.button = button;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        button.setImageResource(R.drawable.yellow);
        dialog = ProgressDialog.show(context, "",
                "Doing test. Please wait...", false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        listener.setTable(table);
        listener.setCapture(true);
        try {
            sleep(10000);
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
