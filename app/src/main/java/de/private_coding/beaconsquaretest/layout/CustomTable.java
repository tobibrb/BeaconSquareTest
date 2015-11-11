package de.private_coding.beaconsquaretest.layout;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import de.private_coding.beaconsquaretest.R;
import de.private_coding.beaconsquaretest.listener.BeaconListener;
import de.private_coding.beaconsquaretest.task.CaptureTask;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 15:25.
 */
public class CustomTable extends TableLayout {

    public CustomTable(final Context context, int height, int width) {
        super(context);

        // Display metric
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        // get dp in px
        Resources r = context.getResources();
        int heightInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpWidth/width,
                r.getDisplayMetrics()
        );
        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics()
        );

        // Parameter for Views
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, heightInPx, 1f);
        buttonParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

        // Set parameter for TableLayout
        this.setLayoutParams(tableParams);
        this.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        this.setShrinkAllColumns(true);
        this.setStretchAllColumns(true);

        // fill row and columns with ImageButtons
        for (int i = 0; i < height; i++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(rowParams);
            for (int j = 0; j < width; j++) {
                final ImageButton button = new ImageButton(context);
                button.setLayoutParams(buttonParams);
                button.setImageResource(R.drawable.red);
                button.setScaleType(ImageView.ScaleType.CENTER);
                final int finalI = i;
                final int finalJ = j;

                // Add OnClickListener for each ImageButton
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CaptureTask(context, button, String.format("%s/%s", finalI, finalJ), BeaconListener.getInstance()).execute();
                    }
                });
                row.addView(button);
            }
            this.addView(row);
        }
    }

}