package lp;

import lp.business.dto.Episode;
import lp.frontend.StartApp;
import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

public class Manager {

    private final FileService fileService = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();

    public Manager() {
        // get IMPORT file
        if (!fileService.getFile(TextEnum.IMPORT_FILE.getText()).exists()) {
            // get path from TextInputDialog
            dialogService.useTextInputDialog(TextEnum.FILE_NOT_FOUND_TITLE.getText(), TextEnum.FILE_NOT_FOUND_MESSAGE.getText());
//            // fill json file
//            fileService.setDataForJSON(pathOfFiles, TextEnum.IMPORT_FILE.getText());
        } else {
            Episode episode = fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Episode.class);
            StartApp.setImportedEpisode(episode);
            javafx.application.Application.launch(StartApp.class);
        }
    }

    public static void main(String[] args) {
        new Manager();
    }
}
