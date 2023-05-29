package lp.serviceimpl;

import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileServiceImpl implements FileService {

    private static FileServiceImpl fileService;

    public static FileServiceImpl getInstance() {
        if (fileService == null) {
            fileService = new FileServiceImpl();
        }
        return fileService;
    }

    @Override
    public void setDataForJSON(String pathOfFiles, String pathForJSON) {
        File rootFile = new File(pathOfFiles);
        if (!rootFile.exists()) {
            return;
        }
        Map<String, Map> episodes = new HashMap<>();
        getData(rootFile, episodes);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(pathForJSON).toFile(), episodes);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public void getData(File file, Map<String, Map> episodes) {
        if (excludeFiles(file.getName())) {
            return;
        }
        episodes.put(file.getName(), new HashMap());
        if (!file.isDirectory() || file.listFiles().length == 0) {
            return;
        }
        for (File subFile : file.listFiles()) {
            getData(subFile, episodes.get(file.getName()));
        }
    }

    @Override
    public File getFile(String text) {
        return new File(text);
    }

    @Override
    public <T> T loadJSON(String path, Class<T> returnedClass) {
        T result = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(Paths.get(path).toFile(), returnedClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean excludeFiles(String fileName) {
        String[] excludedFiles = new String[]{"$RECYCLE.BIN", "System Volume Information", "TODO", ".jpg"};
        for (String exFile : excludedFiles) {
            if (fileName.endsWith(exFile)) {
                return true;
            }
        }
        return false;
    }
}
