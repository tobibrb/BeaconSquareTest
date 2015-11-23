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

    private static String csvFile;
    private static BeaconCsvParser sInstance;
    private final String LOGGER = BeaconCsvParser.class.getSimpleName();

    public BeaconCsvParser() {}

    public static BeaconCsvParser getInstance() {
        if (sInstance == null) {
            sInstance = new BeaconCsvParser();
        }
        return sInstance;
    }

    public static void setCsvFile(int height, int width) {
        BeaconCsvParser.csvFile = android.os.Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/scan_" + height + "x" + width + ".csv";
    }

    public static String getCsvFile() {
        return csvFile;
    }

    public void createTestData(int row, int column, int major, int minor, int rssi, Date date) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csvFile, true));
            writer.writeNext(new String[]{
                    String.valueOf(row),
                    String.valueOf(column),
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
            if (strings[0].equals(String.valueOf(row)) && strings[1].equals(String.valueOf(column))) {
                data.add(new BeaconTestData(
                        Integer.parseInt(strings[0]),
                        Integer.parseInt(strings[1]),
                        Integer.parseInt(strings[3]),
                        Integer.parseInt(strings[4]),
                        Integer.parseInt(strings[5]),
                        Long.parseLong(strings[2])));
            }
        }
        return data;
    }

    public void removeTestDataForColumn(int row, int column) {
        List<BeaconTestData> data = new ArrayList<>();
        List<String[]> entries = getAll();
        for (String[] strings : entries) {
            if (!(strings[0].equals(String.valueOf(row)) && strings[1].equals(String.valueOf(column)))) {
                data.add(new BeaconTestData(
                        Integer.parseInt(strings[0]),
                        Integer.parseInt(strings[1]),
                        Integer.parseInt(strings[3]),
                        Integer.parseInt(strings[4]),
                        Integer.parseInt(strings[5]),
                        Long.parseLong(strings[2])));
            }
        }
        try {
            updateCsv(data);
        } catch (IOException e) {
            Log.d(LOGGER, "Something went wrong: " + e.getMessage());
        }
    }

    public boolean removeTestData() {
        File file = new File(csvFile);
        return file.delete();
    }

    private List<String[]> getAll() {
        List<String[]> entries = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(csvFile));
            entries = reader.readAll();
        } catch (IOException e) {
            Log.d(LOGGER, String.format("Failed to read file: %s. Does it exist?", csvFile));
        }
        return entries;
    }

    private void updateCsv(List<BeaconTestData> data) throws IOException {
        File file = new File(csvFile);
        boolean deleted = file.delete();
        List<String[]> update = new ArrayList<>();
        if (!deleted) {
            throw new IOException("CSV File can't be deleted");
        }
        for (BeaconTestData beacon : data) {
            update.add(new String[]{
                    String.valueOf(beacon.getRow()),
                    String.valueOf(beacon.getColumn()),
                    String.valueOf(beacon.getDate().getTime()),
                    String.valueOf(beacon.getMajor()),
                    String.valueOf(beacon.getMinor()),
                    String.valueOf(beacon.getRssi())
            });
        }
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csvFile, true));
            writer.writeAll(update);
            writer.close();
        } catch (IOException e) {
            Log.d(LOGGER, "Can't write file!");
        }
    }
}
