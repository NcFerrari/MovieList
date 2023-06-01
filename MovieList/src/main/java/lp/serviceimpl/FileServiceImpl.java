package lp.serviceimpl;

import lp.business.dto.Episode;
import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class FileServiceImpl implements FileService {

    private static FileServiceImpl fileService;

    public static FileServiceImpl getInstance() {
        if (fileService == null) {
            fileService = new FileServiceImpl();
        }
        return fileService;
    }

    @Override
    public void writeDataToJSON(String pathOfFiles, String pathForJSON) {
        File rootFile = new File(pathOfFiles);
        if (!rootFile.exists()) {
            return;
        }
        Episode episode = new Episode(rootFile.getName());
        fillEpisode(rootFile, episode);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(pathForJSON).toFile(), episode);
        } catch (IOException exp) {
            exp.printStackTrace();
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

    private void fillEpisode(File file, Episode episode) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                if (excludeFiles(subFile.getName())) {
                    continue;
                }
                String subFileName = subFile.isDirectory() ? subFile.getName() : subFile.getName().substring(0, subFile.getName().length() - 4);
                Episode subEpisode = new Episode(subFileName);
                if (subFile.isDirectory()) {
                    fillEpisode(subFile, subEpisode);
                }
                episode.getSubEpisodes().put(subFileName, subEpisode);
            }
        }
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
