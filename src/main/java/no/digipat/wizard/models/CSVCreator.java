package no.digipat.wizard.models;

import com.github.opendevl.JFlat;
import com.google.gson.Gson;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.Results;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Base64;



/**
 * Utility class for converting analysis results to CSV.
 * 
 * @author Kent Are Torvik
 *
 */
public final class CSVCreator {
    
    private CSVCreator() {
        throw new UnsupportedOperationException("This is a utility class.");
    }
    
    /**
     * Converts a list of results to a CSV string.
     * 
     * @param results the results
     * @param analyzeType the type of analysis that should be included. If {@code null},
     * all analyses will be included.
     * 
     * @return the CSV string
     * 
     * @throws IOException if an I/O error occurs
     */
    public static String toCSV(List<Results> results, String analyzeType) throws IOException {
        List<Results> curatedResultList;
        if (analyzeType == null) {
            curatedResultList = results;
        } else {
            curatedResultList = new ArrayList<>();
            for (Results result : results) {
                for (AnalysisResult aRes : result.getResults()) {
                    if (aRes.getName().equals(analyzeType)) {
                        curatedResultList.add(
                                new Results()
                                .setAnnotationId(result.getAnnotationId())
                                .setResults(new ArrayList<AnalysisResult>() {
                                    {
                                        add(aRes);
                                    }
                                })
                        );
                    }
                }
            }
        }

        String jsonstring = CSVCreator.toJsonString(curatedResultList);
        JFlat flat = new JFlat(jsonstring);
        try {
            flat.json2Sheet().headerSeparator("_").getJsonAsSheet();
        } catch (Exception e) {
            throw new RuntimeException(e);
            // This should never happen
        }
        // TODO surely there's a better way to do this than to write
        // to a temporary file...
        File file = File.createTempFile("csv-export", ".csv");
        flat.write2csv(file.getAbsolutePath());
        String base64String = CSVCreator.encodeFileToBase64Binary(file);
        file.delete();
        return base64String;
    }

    private static String toJsonString(List<Results> results) {
        Gson gson = new Gson();
        return gson.toJson(results, List.class);
    }

    /**
     * Method used to encode the file to base64 binary format.
     * @param file
     * @return encoded file format
     */
    private static String encodeFileToBase64Binary(File file) throws IOException {
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStreamReader.read(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
