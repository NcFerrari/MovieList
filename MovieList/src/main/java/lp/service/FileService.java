package lp.service;

import java.io.File;
import java.util.Map;

public interface FileService {

    File loadJSON(String path);

    Map readJSONFile(File jsonFile);

    File createFile(String text);

    void loadDataForJSON(String path);
}
