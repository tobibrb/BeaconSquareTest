package de.private_coding.beaconsquaretest.csvparser;

import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Bartz, Tobias on 11.11.2015 at 22:12.
 */
public class BeaconCsvParser {

    private String csv;
    private static BeaconCsvParser sInstance;
    private final String LOGGER = BeaconCsvParser.class.getSimpleName();

    public BeaconCsvParser() {
        this.csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/scan.csv";
    }

    public static BeaconCsvParser getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconCsvParser();
        }
        return sInstance;
    }

    public void createTestData(String rowColumn, int major, int minor, int rssi, Date date) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv, true));
            writer.writeNext(new String[]{
                    rowColumn,
                    String.valueOf(date.getTime()),
                    String.valueOf(major),
                    String.valueOf(minor),
                    String.valueOf(rssi)
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BeaconTestData> getTestData(int row, int column) {
        List<BeaconTestData> data = new ArrayList<>();
        List<String[]> entries = getAll();
        for (String[] strings : entries) {
            if (strings[0].equals(String.format("%s/%s", row, column))) {
                data.add(new BeaconTestData(strings[0], Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Long.parseLong(strings[1])));
            }
        }
        return data;
    }

    public void removeTestDataForColumn(int row, int column) {
        List<BeaconTestData> data = new ArrayList<>();
        List<String[]> entries = getAll();
        for (String[] strings : entries) {
            if (!strings[0].equals(String.format("%s/%s", row, column))) {
                data.add(new BeaconTestData(strings[0], Integer.parseInt(strings[2]), Integer.parseInt(strings[3]), Integer.parseInt(strings[4]), Long.parseLong(strings[1])));
            }
        }
        try {
            updateCsv(data);
        } catch (IOException e) {
            Log.d(LOGGER, "Something went wrong: " + e.getMessage());
        }
    }

    private List<String[]> getAll() {
        List<String[]> entries = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(csv));
            entries = reader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private void updateCsv(List<BeaconTestData> data) throws IOException {
        File file = new File(csv);
        boolean deleted = file.delete();
        List<String[]> update = new ArrayList<>();
        if (!deleted) {
            throw new IOException("CSV File cannot be deleted");
        }
        for (BeaconTestData beacon : data) {
            update.add(new String[]{
                    beacon.getRowColumn(),
                    String.valueOf(beacon.getDate().getTime()),
                    String.valueOf(beacon.getMajor()),
                    String.valueOf(beacon.getMinor()),
                    String.valueOf(beacon.getRssi())
            });
        }
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv, true));
            writer.writeAll(update);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
