package de.private_coding.beaconsquaretest.csvparser;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 22:12.
 */
public class BeaconCsvParser {

    private String csv;

    public BeaconCsvParser () {
        this.csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/scan.csv";
    }

    public static BeaconCsvParser newInstance() {
        return new BeaconCsvParser();
    }

    public List<BeaconTestData> parseTestData(int row, int column) {
        List<BeaconTestData> data = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(csv));
            List<String[]> entries = reader.readAll();
            for (String[] strings : entries) {
                if (strings[0].equals(String.format("%s/%s", row, column))) {
                    data.add(new BeaconTestData(strings[0], Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Long.parseLong(strings[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            return data;
    }
}
