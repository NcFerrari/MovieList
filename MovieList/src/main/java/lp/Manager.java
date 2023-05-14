package lp;

import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

import java.io.File;

public class Manager {

    private final FileService fileTool = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();

    public Manager() {
        String path = dialogService.getInputDialog(TextEnum.FILE_NOT_FOUND_TITLE.getText(), TextEnum.FILE_NOT_FOUND_MESSAGE.getText());

        setInitialDataFromImportFile(path);
        fileTool.loadDataForJSON(TextEnum.DEFAULT_ROOT_PATH.getText());
    }

    private void setInitialDataFromImportFile(String path) {
        File jsonFile = fileTool.loadJSON(TextEnum.IMPORT_FILE.getText());
        if (!jsonFile.exists()) {
            jsonFile = fileTool.createFile(TextEnum.IMPORT_FILE.getText());
            fileTool.loadDataForJSON(path);
        }
    }

    public static void main(String[] args) {
        new Manager();
    }
}
