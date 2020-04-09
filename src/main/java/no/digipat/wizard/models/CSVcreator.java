package no.digipat.wizard.models;

import com.github.opendevl.JFlat;
import com.google.gson.Gson;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.Results;
import no.digipat.wizard.models.startanalysis.AnalysisPostBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Base64;



public class CSVcreator {


    public static String toCSV(List<Results> results, String path, String analyzeType) throws Exception {
        List<Results> curatedResultList;
        if(analyzeType == null) {
            curatedResultList = results;
        } else {
            curatedResultList = new ArrayList<>();
            for(Results result : results) {
                for(AnalysisResult aRes : result.getResults()) {
                    if(aRes.getName().equals(analyzeType)) {
                        curatedResultList.add(new Results().setAnnotationId(result.getAnnotationId()).setResults(new ArrayList<AnalysisResult>(){{add(aRes);}}));
                    }
                }
            }
        }

        System.out.println(curatedResultList);
        String jsonstring = CSVcreator.toJsonString(curatedResultList);
        System.out.println(jsonstring);
        JFlat flat = new JFlat(jsonstring);
        flat.json2Sheet().headerSeparator("_").getJsonAsSheet();
        UUID uuid = UUID.randomUUID();
        String filePath = path+"/"+uuid+".csv";
        flat.write2csv(filePath);
        File f = new File(filePath);
        String base64String = CSVcreator.encodeFileToBase64Binary(f);
        f.delete();
        return base64String;
    }

    public static String toJsonString(List<Results> results ) {
        Gson gson = new Gson();
        return gson.toJson(results, List.class);
    }

    /**
     * Method used for encode the file to base64 binary format
     * @param file
     * @return encoded file format
     */
    public static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encodedfile;
    }
}
