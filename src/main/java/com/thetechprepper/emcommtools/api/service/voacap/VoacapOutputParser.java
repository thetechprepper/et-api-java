package com.thetechprepper.emcommtools.api.service.voacap;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VoacapOutputParser {

    /**
     * Parses a VOACAP output file (typically named {@code voacapx.out}) and extracts
     * hourly prediction data into a structured list of {@link PredictionHour} objects.
     *
     * <p>
     * Each prediction hour record in the file is read sequentially and transformed
     * into a {@code PredictionHour} instance containing frequency and signal data
     * for that specific hour. The resulting list represents the full 24-hour
     * propagation prediction contained in the VOACAP output.
     * </p>
     *
     *
     * @param outputFilePath the absolute or relative path to the voacapx.out file
     * @return a list of {@link PredictionHour} objects, one for each hour of the VOACAP prediction.
     * @throws FileNotFoundException if the specified output file does not exist or cannot be accessed.
     * @throws IOException if an I/O error occurs while reading the file.
     */
    public List<PredictionHour> parse(final String outputFilePath) throws 
            FileNotFoundException, IOException  {

        List<PredictionHour> hours = new ArrayList<>();

        Path path = Paths.get(outputFilePath);
        if (!Files.exists(path)) {
	    throw new FileNotFoundException("File not found: " + path);
        }

        boolean inPredictionHour = false;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            List<String> curFreqList = null;
            PredictionHour curPredictionHour = null;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (isPredictionStart(line)) {
                    inPredictionHour = true;
                    curPredictionHour = new PredictionHour();
                }

                if (line.contains("SNRxx")) {
                    inPredictionHour = false;
                    hours.add(curPredictionHour);
                }

                if (isPredictionData(line)) {
		    // Parse the frequency line (FREQ) for the current hour
                    if (line.endsWith("FREQ")) {
                        List<String> freqs = parseLine(line);

                        // column 0  - 24-hour as a double (1.0)
                        // column 1  - Maximum Usable Frequency (MUF) as a double (14.1)
                        // column 2  - first frequency
                        // column 10 - last frequency
			// column 11 - Empty unused frequency
			// column 12 - Empty unused frequency

                        // Only grab the columns with the first through the last frequency
                        curFreqList = freqs.subList(2, 11);
                    }

		    // Parse the reliability line (REL) for the current hour
                    if (line.contains("REL")) {
                        // Only grab reliability values that map to ordered list of frequencies
                        List<String> curRelList = parseLine(line).subList(1, 10);

                        for (int i = 0; i <= 8; i++) {
                            curPredictionHour.addFrequency(
                                Double.valueOf(curFreqList.get(i)),
                                Double.valueOf(curRelList.get(i))
                            );
                        }
                    }
                }
            }
        }

	return hours;
    }

    private boolean isPredictionStart(final String line) {
        return (line != null) && line.matches(".*FREQ$");
    }

    private boolean isPredictionData(final String line) {
        return (line != null) && line.matches(".*(FREQ|S DBW |REL   |SNRxx )$");
    }

    public List<String> parseLine(final String line) {
        List<String> fields = new ArrayList<>();

        if (null == line) {
            return fields;
        }

        fields = Arrays.stream(line.trim().split("\\s+"))
            .map(String::trim)
            .collect(Collectors.toList());

        return fields;
    }
}
