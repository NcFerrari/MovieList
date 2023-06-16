package lp.service;

import lp.business.dto.Episode;

import java.io.File;

public interface FileService {

    Episode getEpisodeObjectFromFileSystem(String pathOfFiles);

    void writeDataToJSON(String pathForJSON, Episode episode);

    File getFile(String text);

    <T> T loadJSON(String path, Class<T> returnedClass);

    void copyFilesTo(String path, Episode episode);

    void addToFile(String pathForFile, String text);

    String getNoteFromJSON(String pathForFile);
}
