package de.private_coding.beaconsquaretest.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.private_coding.beaconsquaretest.R;
import de.private_coding.beaconsquaretest.adapter.BeaconDataAdapter;
import de.private_coding.beaconsquaretest.csvparser.BeaconCsvParser;
import de.private_coding.beaconsquaretest.csvparser.BeaconTestData;
import de.private_coding.beaconsquaretest.layout.CustomTable;
import de.private_coding.beaconsquaretest.task.CaptureTask;

/**
 * Created by Bartz, Tobias on 13.11.2015 at 09:56.
 */
public class TestDetailsDialogFragment extends DialogFragment {

    private BeaconCsvParser parser;

    public static TestDetailsDialogFragment newInstance() {
        return new TestDetailsDialogFragment();
    }

    public TestDetailsDialogFragment() {
        this.parser = BeaconCsvParser.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final int row = bundle.getInt("row", 0);
        final int column = bundle.getInt("column", 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.testdetails_dialog, null);

        TextView uniqueBeacons = (TextView) v.findViewById(R.id.unique_beacons);
        TextView totalData = (TextView) v.findViewById(R.id.total_data);
        ListView beaconDetails = (ListView) v.findViewById(R.id.beacon_listView);

        List<BeaconTestData> all = parser.getTestData(row, column);

        uniqueBeacons.setText(String.valueOf(getUniqueBeaconCount(all)));
        totalData.setText(String.valueOf(all.size()));

        BeaconDataAdapter adapter = new BeaconDataAdapter(getActivity());
        beaconDetails.setAdapter(adapter);
        adapter.addAll(sortBeaconData(all));

        builder.setMessage(String.format("Test details for %s/%s", row, column));
        builder.setView(v);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Start test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CaptureTask(getActivity(), String.format("%s/%s", row, column)).execute();
            }
        });
        builder.setNeutralButton("Clear Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parser.removeTestDataForColumn(row, column);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomTable.getImageButton(String.format("%s/%s", row, column)).setImageResource(R.drawable.red);
                    }
                });
            }
        });

        return builder.create();
    }

    private int getUniqueBeaconCount(List<BeaconTestData> list) {
        List<String> tempStrings = new ArrayList<>();
        for (BeaconTestData data : list) {
            if (!tempStrings.contains(String.format("%s/%s", data.getMajor(), data.getMinor()))) {
                tempStrings.add(String.format("%s/%s", data.getMajor(), data.getMinor()));
            }
        }
        return tempStrings.size();
    }

    private List<List<BeaconTestData>> sortBeaconData(List<BeaconTestData> list) {
        List<List<BeaconTestData>> ret = new ArrayList<>();
        Map<String, List<BeaconTestData>> map = new HashMap<>();
        for (BeaconTestData data : list) {
            if (map.containsKey(String.format("%s/%s", data.getMajor(), data.getMinor()))) {
                map.get(String.format("%s/%s", data.getMajor(), data.getMinor())).add(data);
            } else {
                List<BeaconTestData> tempList = new ArrayList<>();
                tempList.add(data);
                map.put(String.format("%s/%s", data.getMajor(), data.getMinor()), tempList);
            }
        }
        for (String key : map.keySet()) {
            ret.add(map.get(key));
        }
        return ret;
    }

}
