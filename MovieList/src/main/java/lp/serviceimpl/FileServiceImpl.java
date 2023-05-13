package lp.serviceimpl;

import lp.business.dto.Episode;
import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileServiceImpl implements FileService {

    private static FileServiceImpl fileService;

    public static FileServiceImpl getInstance() {
        if (fileService == null) {
            fileService = new FileServiceImpl();
        }
        return fileService;
    }

    public File loadJSON(String path) {
        File jsonFile = new File(path);
        return jsonFile;
    }

    public Map readJSONFile(File jsonFile) {
        Map<String, List<Episode>> resultMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(jsonFile, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("R");
        System.out.println(mapper);
        return resultMap;
    }
}
