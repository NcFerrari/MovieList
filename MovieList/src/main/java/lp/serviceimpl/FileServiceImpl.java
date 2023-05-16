package lp.serviceimpl;

import lp.business.dto.Episode;
import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
        Episode episode = new Episode();
        getData(rootFile, episode);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(pathForJSON).toFile(), episode);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public void getData(File file, Episode episode) {
        episode.setTitle(file.isDirectory() ? file.getName() : file.getName().substring(0, file.getName().length() - 4));
        if (!file.isDirectory()) {
            return;
        }
        for (File subFile : file.listFiles()) {
            Episode subEpisode = new Episode();
            episode.getEpisodes().add(subEpisode);
            getData(subFile, subEpisode);
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
        String[] excludedFiles = new String[]{"$RECYCLE.BIN", "System Volume Information", "TODO"};
        for (String exFile : excludedFiles) {
            if (exFile.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
