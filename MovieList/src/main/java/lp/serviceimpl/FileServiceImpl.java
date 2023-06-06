package lp.serviceimpl;

import lp.business.dto.Episode;
import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileServiceImpl implements FileService {

    private final static DialogService dialogService = DialogServiceImpl.getInstance();
    private final int[] counter = new int[]{0};

    private static FileServiceImpl fileService;

    public static FileServiceImpl getInstance() {
        if (fileService == null) {
            fileService = new FileServiceImpl();
        }
        return fileService;
    }


    @Override
    public void writeDataToJSON(String pathForJSON, Episode episode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(Paths.get(pathForJSON).toFile(), episode);
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
    }

    @Override
    public Episode getEpisodeObjectFromFileSystem(String pathOfFiles) {
        File rootFile = new File(pathOfFiles);
        if (!rootFile.exists()) {
            return null;
        }
        Episode episode = new Episode(rootFile.getAbsolutePath());
        fillEpisode(rootFile, episode);
        return episode;
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
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
        return result;
    }

    @Override
    public void copyFilesTo(String newPath, Episode episode) {
        File newFilesDir = new File(newPath);
        if (!newFilesDir.exists()) {
            if (TextEnum.NO_TEXT.getText().equals(dialogService.useConfirmDialog(TextEnum.DIR_NOT_EXISTS_TITLE.getText(), TextEnum.DIR_NOT_EXISTS_MESSAGE.getText()))) {
                return;
            }
            newFilesDir.mkdir();
        }
        counter[0] = 0;
        String oldPath = episode.getTitle();
        copyFiles(oldPath, newPath, episode);
    }

    @Override
    public void addToFile(String pathForFile, String text) {
        File file = new File(pathForFile);
        if (!file.exists()) {
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.newLine();
            bw.write(TextEnum.NOTE_SEPARATOR.getText());
            bw.newLine();
            bw.write(text);
            bw.close();
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
    }

    @Override
    public String getNoteFromJSON(String pathForFile) {
        File file = new File(pathForFile);
        if (!file.exists()) {
            return "";
        }
        String note = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean record = false;
            while ((line = br.readLine()) != null) {
                if (record) {
                    note += String.format("%s\n", line);
                } else if (line.equals(TextEnum.NOTE_SEPARATOR.getText())) {
                    record = true;
                }
            }
        } catch (FileNotFoundException exp) {
            dialogService.useErrorDialog(exp);
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
        return note;
    }

    private void copyFiles(String oldPath, String newPath, Episode episode) {
        episode.getSubEpisodes().values().forEach(subEpisode -> {
            if (subEpisode.isSelected()) {
                File sourceFile = new File(oldPath + "/" + subEpisode.getTitle());
                File targetFile = new File(newPath + "/" + subEpisode.getTitle());
                try {
                    if (!targetFile.exists()) {
                        targetFile.getParentFile().mkdirs();
                        Files.copy(sourceFile.toPath(), targetFile.toPath());
                        counter[0]++;
                    }
                } catch (IOException exp) {
                    dialogService.useErrorDialog(exp);
                }
            }
            if (!subEpisode.getSubEpisodes().isEmpty()) {
                copyFiles(oldPath + "/" + subEpisode.getTitle(), newPath + "/" + subEpisode.getTitle(), subEpisode);
            }
        });
        dialogService.useInformationDialog(TextEnum.SUCCESS_COPPIED_TITLE.getText(), TextEnum.SUCCESS_COPPIED_MESSAGE.getText() + counter[0] + TextEnum.FILES.getText());
    }

    private void fillEpisode(File file, Episode episode) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                if (excludeFiles(subFile.getName())) {
                    continue;
                }
                String subFileName = subFile.getName();
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
