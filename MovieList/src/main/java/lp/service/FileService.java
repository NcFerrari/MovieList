package lp.service;

import java.io.File;

public interface FileService {

    void setDataForJSON(String pathOfFiles, String pathForJSON);

    File getFile(String text);

    <T> T loadJSON(String path, Class<T> returnedClass);
}
