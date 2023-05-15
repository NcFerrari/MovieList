package lp.service;

import java.io.File;

public interface FileService {
    void loadDataForJSON(String pathOfFiles, String pathForJSON);

    File getFile(String text);
}
