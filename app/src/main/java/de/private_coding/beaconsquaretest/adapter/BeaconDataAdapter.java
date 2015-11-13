package de.private_coding.beaconsquaretest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.private_coding.beaconsquaretest.R;
import de.private_coding.beaconsquaretest.csvparser.BeaconTestData;

/**
 * Created by Bartz, Tobias on 13.11.2015 at 10:36.
 */
public class BeaconDataAdapter extends ArrayAdapter<List<BeaconTestData>> {
    private Context context;

    public BeaconDataAdapter(Context context) {
        super(context, R.layout.beacon_listview);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.beacon_listview, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.majorMinor = (TextView) v.findViewById(R.id.beacon_major_minor);
            holder.avgRssi = (TextView) v.findViewById(R.id.avg_rssi);
            holder.minRssi = (TextView) v.findViewById(R.id.min_rssi);
            holder.maxRssi = (TextView) v.findViewById(R.id.max_rssi);
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        List<BeaconTestData> list = getItem(position);
        holder.majorMinor.setText(String.format("Beacon %s/%s", list.get(0).getMajor(), list.get(0).getMinor()));
        holder.avgRssi.setText(String.valueOf(getAvg(list)));
        holder.minRssi.setText(String.valueOf(getMin(list)));
        holder.maxRssi.setText(String.valueOf(getMax(list)));
        return v;
    }

    private int getAvg(List<BeaconTestData> list) {
        int ret = 0;
        for (BeaconTestData data : list) {
            ret += data.getRssi();
        }
        if (!list.isEmpty()) {
            ret /= list.size();
        }
        return ret;
    }

    private int getMin(List<BeaconTestData> list) {
        if (!list.isEmpty()) {
            int min = list.get(0).getRssi();
            for (BeaconTestData data : list) {
                min = Math.min(min, data.getRssi());
            }
            return min;
        }
        return 0;
    }

    private int getMax(List<BeaconTestData> list) {
        if (!list.isEmpty()) {
            int max = list.get(0).getRssi();
            for (BeaconTestData data : list) {
                max = Math.max(max, data.getRssi());
            }
            return max;
        }
        return 0;
    }

    private static class ViewHolder {
        TextView majorMinor;
        TextView avgRssi;
        TextView minRssi;
        TextView maxRssi;
    }
}
