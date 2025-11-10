package com.thetechprepper.emcommtools.api.voacap;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class VoacapParser {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java VoacapParser <voacapx.out>");
            System.exit(1);
        }

        Path path = Paths.get(args[0]);
        if (!Files.exists(path)) {
            System.err.println("File not found: " + path);
            System.exit(1);
        }

        List<PredictionHour> records = new ArrayList<>();
        boolean inPredictionHour = false;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            List<String> curFreqList = null;
            PredictionHour curPredictionHour = null;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (isPredictionStart(line)) {
                    //printDebugLine(lineNumber, "PREDICTION_HOUR_START");
                    inPredictionHour = true;
                    curPredictionHour = new PredictionHour();
                }

                if (line.contains("SNRxx")) {
                    //printDebugLine(lineNumber, "PREDICTION_HOUR_END");
                    inPredictionHour = false;
                    records.add(curPredictionHour);
                    curPredictionHour.printFrequencies();
                }

                if (isPredictionData(line)) {
                    if (line.endsWith("FREQ")) {
                        List<String> freqs = parseLine(line);

                        //System.out.println("Hour: " + hour);
                        // column 0  - 24-hour as a double (1.0)
                        // column 1  - MUF
                        // column 2  - first frequency
                        // column 10 - last frequency
			// column 11... unused
                        System.out.println("Hour: " + freqs.get(0));
                        for (int i = 2; i <= 10; i++) {
                            //curPredictionHour.addFrequency(Double.valueOf(cols.get(i)));
                        }

                        // Only grab the columns with the first through the last frequency
                        curFreqList = freqs.subList(2, 10);
                    }

                    if (line.contains("REL")) {
                        // only grab reliability values that map to ordered list of frequencies
                        List<String> curRelList = parseLine(line).subList(1, 9);

                        for (int i = 0; i <= 7; i++) {
                            curPredictionHour.addFrequency(
                                Double.valueOf(curFreqList.get(i)),
                                Double.valueOf(curRelList.get(i))
                            );
                        }
                    }
                }
            }
        }

        System.out.printf("Parsed %d records from %s%n", records.size(), path);
    }

    private static boolean isPredictionStart(final String line) {
        return line.matches(".*FREQ$");
    }

    private static boolean isPredictionData(final String line) {
        return line.matches(".*(FREQ|S DBW |REL   |SNRxx )$");
    }

    private static void printDebugLine(final int lineNumber, final String tokenType) {
        System.out.printf("%3d| %-15s%n", lineNumber, tokenType);
    }

    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();

        if (line == null) {
            return fields;
        }

        fields = Arrays.stream(line.trim().split("\\s+"))
            .map(String::trim)
            .collect(Collectors.toList());

        return fields;
    }
}
