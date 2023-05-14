package lp.serviceimpl;

import lp.business.dto.Episode;
import lp.frontend.TextEnum;
import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    @Override
    public File loadJSON(String path) {
        File jsonFile = new File(path);
        return jsonFile;
    }

    @Override
    public Map readJSONFile(File jsonFile) {
        Map<String, List<Episode>> resultMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(jsonFile, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public File createFile(String title) {
        try {
            File newFile = new File(title);
            newFile.createNewFile();
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void loadDataForJSON(String path) {
        File cDir = new File(path);
        Episode rootDir = new Episode();
        rootDir.setTitle(cDir.getName());
        rootDir.setDirectory(true);
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
            mapper.writeValue(Paths.get(TextEnum.IMPORT_FILE.getText()).toFile(), rootDir);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
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
