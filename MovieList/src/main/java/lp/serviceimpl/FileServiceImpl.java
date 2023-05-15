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
        File cDir = new File(pathOfFiles);
        if (!cDir.exists()) {
            return;
        }
        Episode rootDir = new Episode();
        rootDir.setTitle(cDir.getName());
        rootDir.setDirectory(cDir.isDirectory());
        for (File category : cDir.listFiles()) {
            if (excludeFiles(category.getName())) {
                continue;
            }
            Episode categoryEpisode = new Episode();
            categoryEpisode.setTitle(category.getName());
            categoryEpisode.setDirectory(category.isDirectory());
            rootDir.getEpisodes().add(categoryEpisode);
            if (category.isDirectory() && category.listFiles() != null) {
                for (File dir : category.listFiles()) {
                    Episode episode = new Episode();
                    String name = "";
                    for (int i = 0; i < dir.getName().split("\\.").length - 1; i++) {
                        name += dir.getName().split("\\.")[i];
                    }
                    episode.setTitle(name);
                    episode.setDirectory(dir.isDirectory());
                    categoryEpisode.getEpisodes().add(episode);
                    if (dir.isDirectory() && dir.listFiles() != null) {
                        for (File subDir : dir.listFiles()) {
                            Episode subEpisode = new Episode();
                            String subName = "";
                            for (int i = 0; i < subDir.getName().split("\\.").length - 1; i++) {
                                subName += subDir.getName().split("\\.")[i];
                            }
                            subEpisode.setTitle(subName);
                            subEpisode.setDirectory(subDir.isDirectory());
                            episode.getEpisodes().add(subEpisode);
                        }
                    }
                }
            }
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(pathForJSON).toFile(), rootDir);
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
