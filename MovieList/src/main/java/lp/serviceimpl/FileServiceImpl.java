package lp.serviceimpl;

import lp.business.dto.Episode;
import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class FileServiceImpl implements FileService {

    //=======================SINGLETON=========================
    private static FileServiceImpl fileService;

    public static FileServiceImpl getInstance() {
        if (fileService == null) {
            fileService = new FileServiceImpl();
        }
        return fileService;
    }

    private FileServiceImpl() {
    }
    //=======================SINGLETON=========================


    //=======================ATTRIBUTES========================
    private static final DialogService dialogService = DialogServiceImpl.getInstance();
    private final int[] counter = new int[]{0};
    private String[] excludedFiles;
    //=======================ATTRIBUTES========================


    //=======================METHODS===========================
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
    public void copyFilesTo(String newPath, Episode episode) {
        File newFilesDir = new File(newPath);
        if (!newFilesDir.exists()) {
            String answer = dialogService.useConfirmDialog(
                    TextEnum.DIR_NOT_EXISTS_TITLE.getText(),
                    TextEnum.DIR_NOT_EXISTS_MESSAGE.getText());
            if (TextEnum.NO_TEXT.getText().equals(answer)) {
                return;
            }
            boolean wasSuccessful = newFilesDir.mkdir();
            if (!wasSuccessful) {
                dialogService.useErrorDialog(new Exception(TextEnum.DIR_NOT_CREATED.getText()));
            }
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
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            bufferedWriter.newLine();
            bufferedWriter.write(TextEnum.NOTE_SEPARATOR.getText());
            bufferedWriter.newLine();
            bufferedWriter.write(text);
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    @Override
    public Episode getEpisodeObjectFromFileSystem(String pathOfFiles) {
        File rootFile = new File(pathOfFiles);
        if (!rootFile.exists()) {
            return null;
        }
        Episode episode = new Episode(rootFile.getAbsolutePath());
        loadExcludedFile();
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
    public String getNoteFromJSON(String pathForFile) {
        File file = new File(pathForFile);
        if (!file.exists()) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String fileLine;
            boolean noteIndex = false;
            while ((fileLine = bufferedReader.readLine()) != null) {
                if (noteIndex) {
                    stringBuilder.append(fileLine).append("\n");
                } else if (fileLine.equals(TextEnum.NOTE_SEPARATOR.getText())) {
                    noteIndex = true;
                }
            }
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
        return stringBuilder.toString();
    }
    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private void copyFiles(String oldPath, String newPath, Episode episode) {
        episode.getSubEpisodes().values().forEach(subEpisode -> {
            if (subEpisode.isSelected() && subEpisode.getSubEpisodes().isEmpty()) {
                File sourceFile = new File(oldPath + TextEnum.SEPARATOR.getText() + subEpisode.getTitle());
                File targetFile = new File(newPath + TextEnum.SEPARATOR.getText() + subEpisode.getTitle());
                try {
                    if (!targetFile.exists()) {
                        boolean wasSuccessful = targetFile.getParentFile().mkdirs();
                        if (!wasSuccessful) {
                            dialogService.useErrorDialog(new Exception(TextEnum.DIRS_NOT_CREATED.getText()));
                        }
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
        dialogService.useInformationDialog(
                TextEnum.SUCCESS_COPPIED_TITLE.getText(),
                TextEnum.SUCCESS_COPPIED_MESSAGE.getText() + counter[0] + TextEnum.FILES.getText());
    }

    private void fillEpisode(File file, Episode episode) {
        if (file.isDirectory()) {
            File[] sortedFiles = sortingArrayWithNumericOrder(Objects.requireNonNull(file.listFiles()));
            for (File subFile : sortedFiles) {
                if (excludeFiles(subFile.getName())) {
                    continue;
                }
                String subFileName = subFile.getName();
                Episode subEpisode = new Episode(subFileName);
                if (subFile.isDirectory()) {
                    fillEpisode(subFile, subEpisode);
                } else {
                    subEpisode.setSize(subFile.length());
                }
                episode.getSubEpisodes().put(subFileName, subEpisode);
            }
        }
    }

    /**
     * Count that array is alphabetic sorted
     * This method just make text started with number to be numerical sorted
     *
     * @param files is alphabetic sorted array
     */
    private File[] sortingArrayWithNumericOrder(File[] files) {

        Map<Integer, List<File>> integerFirstLetterMap = new TreeMap<>();
        for (File file : files) {
            if (file.getName().substring(0, 1).matches("\\d")) {
                int key = getIntegerKey(file.getName().split(""));
                integerFirstLetterMap.putIfAbsent(key, new ArrayList<>());
                integerFirstLetterMap.get(key).add(file);
            }
        }

        int j = 0;
        for (List<File> fileList : integerFirstLetterMap.values()) {
            for (File file : fileList) {
                files[j++] = file;
            }
        }
        return files;
    }

    private Integer getIntegerKey(String[] splitName) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String letter : splitName) {
            if (letter.matches("\\d")) {
                stringBuilder.append(letter);
            } else {
                break;
            }
        }
        return Integer.parseInt(stringBuilder.toString());
    }

    private void loadExcludedFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream(
                                TextEnum.EXCLUDED_FILES_FILE_NAME.getText())), StandardCharsets.UTF_8))) {
            String fileLine;
            while ((fileLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(fileLine).append(TextEnum.SEPARATOR.getText());
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        } catch (IOException exp) {
            dialogService.useErrorDialog(exp);
        }
        excludedFiles = stringBuilder.toString().split(TextEnum.SEPARATOR.getText());
    }

    private boolean excludeFiles(String fileName) {
        for (String exFile : excludedFiles) {
            if (fileName.endsWith(exFile)) {
                return true;
            }
        }
        return false;
    }
    //=======================PRIVATE METHODS===================
}