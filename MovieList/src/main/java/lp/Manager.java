package lp;

import lp.frontend.StartApp;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

import java.io.File;

public class Manager {

    private final String importFile = "IMPORTe.json";
    private final FileService fileTool = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();

    public Manager() {
        dialogService.getInputDialog("test", "test");
        setInitialDataFromImportFile();
    }

    private void setInitialDataFromImportFile() {
        File jsonFile = fileTool.loadJSON(importFile);
        if (!jsonFile.exists()) {

        }
    }

    public static void main(String[] args) {
        javafx.application.Application.launch(StartApp.class);
        new Manager();
    }
}
