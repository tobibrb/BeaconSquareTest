package de.private_coding.beaconsquaretest.layout;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.private_coding.beaconsquaretest.R;
import de.private_coding.beaconsquaretest.csvparser.BeaconCsvParser;
import de.private_coding.beaconsquaretest.csvparser.BeaconTestData;
import de.private_coding.beaconsquaretest.fragment.TestDetailsDialogFragment;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 15:25.
 */
public class CustomTable extends TableLayout {

    private static Map<String, ImageButton> buttonMap = new HashMap<>();

    public static ImageButton getImageButton(String key) {
        return buttonMap.get(key);
    }

    public static Map<String, ImageButton> getImageButtons() {
        return buttonMap;
    }

    public CustomTable(final AppCompatActivity activity, final Context context, int height, int width) {
        super(context);

        // CSV Parser

        BeaconCsvParser parser = BeaconCsvParser.getInstance();

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
                List<BeaconTestData> list = parser.getTestData(i,j);
                final ImageButton button = new ImageButton(context);
                button.setLayoutParams(buttonParams);
                if (list.isEmpty()) {
                    button.setImageResource(R.drawable.red);
                } else {
                    button.setImageResource(R.drawable.yellow);
                }
                button.setScaleType(ImageView.ScaleType.CENTER);
                final int finalI = i;
                final int finalJ = j;

                // Add OnClickListener for each ImageButton
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment dialog = TestDetailsDialogFragment.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putInt("row", finalI);
                        bundle.putInt("column", finalJ);
                        dialog.setArguments(bundle);
                        dialog.show(activity.getSupportFragmentManager(), "TestDetailsDialogFragment");
                        //new CaptureTask(context, button, String.format("%s/%s", finalI, finalJ), BeaconListener.getInstance()).execute();
                    }
                });
                buttonMap.put(String.format("%s/%s", i, j), button);
                row.addView(button);
            }
            this.addView(row);
        }
    }

}
