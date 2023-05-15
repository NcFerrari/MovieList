package lp;

import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

public class Manager {

    private final FileService fileTool = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();

    public Manager() {
        // get IMPORT file
        if (!fileTool.getFile(TextEnum.IMPORT_FILE.getText()).exists()) {
            // get path from TextInputDialog
            String pathOfFiles = dialogService.getInputDialog(TextEnum.FILE_NOT_FOUND_TITLE.getText(), TextEnum.FILE_NOT_FOUND_MESSAGE.getText());
            // fill json file
            fileTool.setDataForJSON(pathOfFiles, TextEnum.IMPORT_FILE.getText());
        }


    }

    public static void main(String[] args) {
        new Manager();
    }
}
